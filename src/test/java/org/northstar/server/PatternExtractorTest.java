package org.northstar.server;

import org.junit.Assert;
import org.junit.Test;
import org.northstar.servers.routing.PatternExtractor;

public class PatternExtractorTest {

    @Test
    public void testPattern(){
        PatternExtractor pe=new PatternExtractor("/status/{abcd}/{def}");
        PatternExtractor.Match m=pe.match("/status/test/xyz");
        Assert.assertEquals(1,m.getConstantMatchCount());
        Assert.assertTrue(m.isMatched());
    }
}
