package org.northstar.servers.routing;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PatternExtractor {


    public static class Match{
        private boolean matched;

        public Map<String, String> getAttributes() {
            return attributes;
        }

        public void setAttributes(Map<String, String> attributes) {
            this.attributes = attributes;
        }

        private Map<String,String> attributes;

        public boolean isMatched() {
            return matched;
        }

        public void setMatched(boolean matched) {
            this.matched = matched;
        }

        public int getConstantMatchCount() {
            return constantMatchCount;
        }

        public void setConstantMatchCount(int constantMatchCount) {
            this.constantMatchCount = constantMatchCount;
        }

        private int constantMatchCount;

    }

    public static class PatternType{
        private boolean constant;

        public boolean isConstant() {
            return constant;
        }

        public void setConstant(boolean constant) {
            this.constant = constant;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        private String value;

        PatternType(boolean constant,String value ){
            this.constant=constant;
            this.value=value;
        }
    }

    protected Pattern pattern;

    private final String origPattern;

    public String getOrigPattern() {
        return origPattern;
    }

    private int tupCount;

    private List<PatternType> postionPatterns = null;

    public PatternExtractor(String origPattern){
        this.origPattern = origPattern;
        this.postionPatterns = new ArrayList<>();
        rewritePattern(origPattern);
    }

    private void addConstant(String base){
        Arrays.stream(base.split("/")).forEach(s->{
            if(!s.isEmpty()){
                postionPatterns.add(new PatternType(true, s));
            }
        });
    }
    private void rewritePattern(String layer){
        StringBuilder param=new StringBuilder();
        for(int i=0;i<layer.length();i++){
            char c = layer.charAt(i);
            if(c=='{'){
                String cs=param.toString();
                if(!cs.isEmpty()) {
                    addConstant(param.toString());
                    param.delete(0, param.length());
                }
            }else if(c=='}'){
                postionPatterns.add(new PatternType(false,param.toString()));
                param.delete(0, param.length());
            }else{
                param.append(c);
            }
        }
        if(param.length()>0) {
            addConstant(param.toString());
        }
        tupCount=postionPatterns.size();
    }

    private boolean match(PatternType pType,String value){
        if(pType.isConstant()){
            return pType.getValue().equals(value);
        }else{
            return true;
        }
    }
    public Match match(String url){
        Match m = new Match();
        List<String> splits= Arrays.stream(url.split("/")).filter(s->!s.isEmpty()).collect(Collectors.toList());
        Map<String,String> attributes = new HashMap<>();
        if(splits.size()==tupCount){
            int cMatch = 0;
            m.setMatched(true);
            for(int i=0;i<postionPatterns.size();i++){
                PatternType pp=postionPatterns.get(i);
                String checkValue=splits.get(i);
                boolean match =match(pp,checkValue);
                if(!match){
                    m.setMatched(false);
                    break;
                }
                if(pp.isConstant()){
                    cMatch++;
                }else{
                    attributes.put(pp.getValue(),checkValue);
                }
                m.setMatched(m.isMatched());
            }
            if(m.isMatched()){
                m.setConstantMatchCount(cMatch);
                m.setAttributes(attributes);
            }
        }
        return m;
    }





}
