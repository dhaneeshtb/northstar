const crypto = require('crypto')
const axios =require('axios');
const ALGORITHM = 'aes-256-gcm';
const TAG_LENGTH = 16;
const KEY_LENGTH = 32;
const ITERATION = 65536;

function generateKeyFiles() {
   const keyPair = crypto.generateKeyPairSync('rsa', {
      modulusLength: 4096,
      publicKeyEncoding: {
         type: 'spki',
         format: 'pem'
      },
      privateKeyEncoding: {
         type: 'pkcs8',
         format: 'pem',
      }
   });
    return keyPair;
}

class GCM {
    constructor(secret,options) {
      this.secret=secret;
      this.options=options;
    }
    getKey(salt) {
        return crypto.pbkdf2Sync(this.secret, salt, this.options.ITERATION_COUNT||ITERATION, this.options.KEY_LENGTH||KEY_LENGTH, 'sha256');
    }


    decrypt(cipherText,salt,iv,aad) {
        const key = this.getKey(salt);
        const authTagLength = this.options.TAG_LENGTH||16;
        const decipher = crypto.createDecipheriv(ALGORITHM, key, iv, { authTagLength:authTagLength });
        decipher.setAutoPadding(true);
        const tl=cipherText.length;
        if(aad){
            decipher.setAAD(aad);
        }
        decipher.setAuthTag(cipherText.slice(tl-authTagLength,tl));
        const dec=decipher.update(cipherText.slice(0,tl-authTagLength),'binary');
        return dec.toString('utf-8')+decipher.final('utf-8');
    }
}

function numberFromBytes(buffer) {
  const bytes = new Uint8ClampedArray(buffer);
  const size = bytes.byteLength;
  let x = 0;
  for (let i = 0; i < size; i++) {
    const byte = bytes[i];
    x *= 0x100;
    x += byte;
  }
  return x;
}


const server="http://localhost:8080"

async function getPublicKey(){
    return (await axios.get(server+"/publickey")).data.publicKey;
}

async function fetchData(){
    const key=await getPublicKey();
    const randomPassword=crypto.randomBytes(20).toString('hex');
    const encryptPass = crypto.publicEncrypt({
                                                   key: key,
                                                   padding: crypto.constants.RSA_PKCS1_OAEP_PADDING
                                                 },Buffer.from(randomPassword)).toString("base64")
    let resp=null;
    try{
        resp = await axios.get(server+"/test/tt/ff/testing",{headers:{
        "X-Client-Token":encryptPass
        }});
    }catch(e){
        console.log(e)
        return;
    }
    const buf = Buffer.from(resp.data,'base64');
    const s = numberFromBytes(buf.slice(0,4))
    const vs = numberFromBytes(buf.slice(4,8))
    console.log("Decrypt with server public key")
    const originalData = crypto.publicDecrypt({
                              key: key,
                              padding: crypto.constants.RSA_PKCS1_PADDING
                          },Buffer.from(buf.slice(8,s+8).toString('utf-8'),'base64'));
    const info = JSON.parse(originalData);
    //console.log(info)
    info.password=randomPassword+info.password
    const gcm=new GCM(info.password,info.constants);
    const db=Buffer.from(buf.slice(s+8),'utf-8')
    const dec = gcm.decrypt(db,Buffer.from(info.salt,'base64'),Buffer.from(info.iv,"base64"),info.aad && Buffer.from(info.aad,"utf8"));
    console.log(dec);
}


async function fetchDataWithClentCert({ publicKey, privateKey }){
    const key=await getPublicKey();
    const randomPassword=crypto.randomBytes(20).toString('hex');
    console.log("Generated Keys")
    const encryptPass = crypto.publicEncrypt({
                                                   key: key,
                                                   padding: crypto.constants.RSA_PKCS1_OAEP_PADDING
                                                 },Buffer.from(randomPassword)).toString("base64")
    const resp = await axios.get(server+"/test/tt/ff/testing",{headers:{
    "X-Client-Cert":Buffer.from(publicKey).toString('base64'),
    "X-Client-Token":encryptPass
    }});
    const buf = Buffer.from(resp.data,'base64');
    const s = numberFromBytes(buf.slice(0,4))
    const vs = numberFromBytes(buf.slice(4,8))
    console.log("Decrypt with server public key")
    const originalData = crypto.privateDecrypt({
                              key: privateKey,
                              padding: crypto.constants.RSA_PKCS1_OAEP_PADDING
                          },Buffer.from(buf.slice(8,s+8).toString('utf-8'),'base64'));
    const info = JSON.parse(originalData);
    console.log(info)
    info.password=randomPassword+info.password
    const gcm=new GCM(info.password,info.constants);
    const db=Buffer.from(buf.slice(s+8),'utf-8')
    const dec = gcm.decrypt(db,Buffer.from(info.salt,'base64'),Buffer.from(info.iv,"base64"),info.aad && Buffer.from(info.aad,"utf8"));
    console.log(dec);
}

//Fetch with client key
fetchData();

//Fetch with client RSA certificate
fetchDataWithClentCert(generateKeyFiles());



