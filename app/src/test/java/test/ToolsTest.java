package test;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import sim.util.Tools;

import static org.junit.Assert.*;

public class ToolsTest {

    @Test
    public void splitCollection() {
        Collection<Object> srcCollection = new ArrayList<>();

        for (int i=0;i<100;i++)
        {
            srcCollection.add(i);
        }

        Float[] fArray = new Float[3];
        fArray[0] = 12f;
        fArray[1] = 13f;
        fArray[2] = 14f;

        Collection<Collection<Object>> result = Tools.splitCollection(fArray, srcCollection, true);

    }

    @Test
    public void splitValue() {
        Long lValue = 1000L;
        Float fValue = 1000F;

        Float[] fArray = new Float[3];
        fArray[0] = 12f;
        fArray[1] = 13f;
        fArray[2] = 14f;

        ArrayList<Long> resultLong = Tools.splitValue(fArray, lValue);

        ArrayList<Float> resultFloat = Tools.splitValue(fArray, fValue);

    }
}