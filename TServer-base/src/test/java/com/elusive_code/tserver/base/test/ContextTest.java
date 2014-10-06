package com.elusive_code.tserver.base.test;

import com.elusive_code.tserver.base.Context;
import com.elusive_code.tserver.base.FinalModificationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.*;

/**
 * @author Vladislav Dolgikh
 */
@RunWith(JUnit4.class)
public class ContextTest {

    public Context prepareContext(){
        Context parent = new Context();
        parent.put("parentRec1",1);
        parent.putFinal("parentRec2", 2);
        parent.put("parentRec3",3);

        Context ctx = new Context(parent);
        ctx.put("rec1",1);
        ctx.putFinal("rec2", 2);
        ctx.put("parentRec3",5);
        return ctx;
    }

    @Test
    public void testGet(){
        Context ctx = prepareContext();
        Context parent = ctx.getParent();

        Assert.assertEquals(1,parent.getChildren().size());

        Assert.assertEquals(3,ctx.size());
        Assert.assertEquals(3,parent.size());

        Assert.assertEquals(1,parent.get("parentRec1"));
        Assert.assertEquals(2,parent.get("parentRec2"));
        Assert.assertEquals(3,parent.get("parentRec3"));

        Assert.assertEquals(1,ctx.get("parentRec1"));
        Assert.assertEquals(2,ctx.get("parentRec2"));
        Assert.assertEquals(5,ctx.get("parentRec3"));
        Assert.assertEquals(1,ctx.get("rec1"));
        Assert.assertEquals(2,ctx.get("rec2"));

        Assert.assertNull(parent.get("rec1"));

        Assert.assertTrue(ctx.isFinal("rec2"));
        Assert.assertFalse(ctx.isFinal("parentRec2"));
        Assert.assertTrue(parent.isFinal("parentRec2"));
    }

    @Test
    public void testIteration(){
        Context ctx = prepareContext();
        Context parent = ctx.getParent();

        Set<String> keys = new HashSet(){{
            add("rec1");
            add("rec2");
            add("parentRec3");
        }};

        for (Map.Entry<String,Object> e: ctx.entrySet()){
            Assert.assertTrue(keys.remove(e.getKey()));
        }

        Assert.assertTrue(keys.isEmpty());
    }

    @Test
    public void testModification(){
        Context ctx = prepareContext();
        Context parent = ctx.getParent();

        Assert.assertEquals(2,parent.get("parentRec2"));
        Assert.assertEquals(2,ctx.get("parentRec2"));
        ctx.put("parentRec2",4);
        Assert.assertEquals(4,ctx.get("parentRec2"));
        Assert.assertEquals(2,parent.get("parentRec2"));

        Iterator<Map.Entry<String,Object>> i = ctx.entrySet().iterator();
        while (i.hasNext()){
            Map.Entry<String,Object> e = i.next();
            if ("rec1".equals(e.getKey())){
                i.remove();
            }
        }

        Assert.assertNull(ctx.get("rec1"));

    }

    @Test
    public void testFinal(){
        Context ctx = prepareContext();
        Context parent = ctx.getParent();

        Assert.assertTrue(ctx.isFinal("rec2"));
        Assert.assertFalse(ctx.isFinal("parentRec2"));
        Assert.assertTrue(parent.isFinal("parentRec2"));


        ctx.remove("rec1");
        try {
            ctx.remove("rec2");
            Assert.fail("FinalModificationException expected");
        } catch (FinalModificationException ex){
        }

        try {
            ctx.put("rec2",0);
            Assert.fail("FinalModificationException expected");
        } catch (FinalModificationException ex){
        }

        try {
            ctx.entrySet().add(new AbstractMap.SimpleEntry<>("rec2",0));
            Assert.fail("FinalModificationException expected");
        } catch (FinalModificationException ex){
        }

        try {
            ctx.entrySet().remove(new AbstractMap.SimpleEntry<>("rec2", 0));
            Assert.fail("FinalModificationException expected");
        } catch (FinalModificationException ex){
        }


        Iterator<Map.Entry<String,Object>> i = ctx.entrySet().iterator();
        while (i.hasNext()){
            Map.Entry<String,Object> e = i.next();
            if ("rec2".equals(e.getKey())){
                try {
                    i.remove();
                    Assert.fail("FinalModificationException expected");
                } catch (FinalModificationException ex){
                }
            }
        }

        Iterator<String> j = ctx.keySet().iterator();
        while (i.hasNext()){
            String e = j.next();
            if ("rec2".equals(e)){
                try {
                    i.remove();
                    Assert.fail("FinalModificationException expected");
                } catch (FinalModificationException ex){
                }
            }
        }

        Iterator k = ctx.values().iterator();
        while (i.hasNext()){
            Object e = k.next();
            if (new Integer(2).equals(e)){
                try {
                    i.remove();
                    Assert.fail("FinalModificationException expected");
                } catch (FinalModificationException ex){
                }
            }
        }



    }
}
