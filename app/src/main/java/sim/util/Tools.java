package sim.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class Tools {

    static private Random s_Random = new Random();

    static public Random Random()
    {
        return s_Random;
    }

    //将集合按照比例分割为指定的两份，输入的集合是剩余的一份，返回的是指定的一份，可以指定从随机点开始分割
    static public Collection<Object> splitCollection(Float splitRate, Collection<Object> rawCollection, boolean bRandomStartPos)
    {
        Collection<Object> result = null;
        try {
            result = rawCollection.getClass().newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        if (result == null)
        {
            return result;
        }

        long nTotalNum = rawCollection.size();
        if (nTotalNum == 0)
        {
            return result;
        }
        long nRestNum = nTotalNum;
        Iterator<Object> Iter = rawCollection.iterator();
        if(bRandomStartPos)
        {
            int nPos = Random().nextInt((int) nTotalNum);

            for (int i=0;i<nPos;i++)
            {
                Iter.next();
            }
        }

        long nNum = (long) (nTotalNum*splitRate);

        for (int n=0; n<nNum;n++)
        {
            if (!Iter.hasNext())
            {
                Iter = rawCollection.iterator();
            }
            Object Item = Iter.next();
            Iter.remove();
            result.add(Item);
        }

        return result;
    }

    //将集合按照比例分割为指定的几份，可以指定从随机点开始分割
    static public ArrayList<Collection<Object>> splitCollection(Float[] splitArray, Collection<Object> rawCollection, boolean bRandomStartPos)
    {
        ArrayList<Collection<Object>> resultArray = new ArrayList<Collection<Object>>(splitArray.length);

        for (int i=0;i<splitArray.length;i++)
        {
            try {
                resultArray.add(rawCollection.getClass().newInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

        //计算总的比例份数
        float fTotal = 0;
        for (Float fValue:splitArray)
        {
            fTotal += fValue;
        }

        long nTotalNum = rawCollection.size();
        if (nTotalNum == 0)
            return resultArray;

        long nRestNum = nTotalNum;
        int nIndex = 0;
        Iterator<Object> Iter = rawCollection.iterator();
        if(bRandomStartPos)
        {
            int nPos = Random().nextInt((int) nTotalNum);

            for (int i=0;i<nPos;i++)
            {
                Iter.next();
            }
        }
        for (Float fValue:splitArray)
        {
            if (nRestNum>=1)
            {
                long nNum = (long) (nTotalNum*(fValue/fTotal));
                Collection<Object> CollectionToInsert = resultArray.get(nIndex);

                for (int n=0; n<nNum;n++)
                {
                    if (!Iter.hasNext())
                    {
                        Iter = rawCollection.iterator();
                    }
                    Object Item = Iter.next();
                    CollectionToInsert.add(Item);
                }
                nRestNum = nRestNum-nNum;
            }
            else
            {
                break;
            }
            nIndex++;
        }

        if (nRestNum != 0)
        {
            Collection<Object> CollectionToInsert = resultArray.get(resultArray.size()-1);

            for (int n=0; n<nRestNum;n++)
            {
                if (!Iter.hasNext())
                {
                    Iter = rawCollection.iterator();
                }
                Object Item = Iter.next();
                CollectionToInsert.add(Item);
            }
        }

        return resultArray;
    }

    static public <T extends Number> ArrayList<T> splitValue (Float[] splitArray, T lValueToSplit)
    {
        boolean bLong = false;
        boolean bFloat = false;
        if(lValueToSplit instanceof Long)
        {
            bLong = true;
        }
        else if (lValueToSplit instanceof Float)
        {
            bFloat = true;
        }
        else
        {
            return null;
        }

        ArrayList<T> resultArray = new ArrayList<T>(splitArray.length);

        for (int i=0;i<splitArray.length;i++)
        {
            T tval = null;
            if (bLong)
            {
                tval = (T) new Long(0);
            }
            else if (bFloat)
            {
                tval = (T) new Float(0);
            }
            resultArray.add(tval);
        }

        //计算总的比例份数
        float fTotal = 0;
        for (Float fValue:splitArray)
        {
            fTotal += fValue;
        }

        T nTotalNum = lValueToSplit;
        T nRestNum = nTotalNum;

        int nIndex = 0;

        for (Float fValue:splitArray)
        {
            if (nRestNum.longValue() >= 1)
            {
                if (bLong)
                {
                    Long lVal = Math.round(nTotalNum.doubleValue()*(fValue/fTotal));
                    T nNum = (T) (Object)(lVal);
                    resultArray.set(nIndex, nNum);
                    nRestNum = (T)(Object)(nRestNum.longValue()-nNum.longValue());
                }
                else if (bFloat)
                {
                    T nNum = (T) (Object)(nTotalNum.floatValue()*(fValue/fTotal));
                    resultArray.set(nIndex, nNum);
                    nRestNum = (T)(Object)(nRestNum.floatValue()-nNum.floatValue());
                }
            }
            else
            {
                break;
            }
            nIndex++;
        }

        if (bLong)
        {
            if (nRestNum.longValue() != 0)
            {
                T ValueLast = resultArray.get(resultArray.size()-1);
                resultArray.set(resultArray.size()-1, (T)(Object)(ValueLast.longValue()+nRestNum.longValue()));
            }
        }
        else if (bFloat)
        {
            if (nRestNum.floatValue() != 0)
            {
                T ValueLast = resultArray.get(resultArray.size()-1);
                resultArray.set(resultArray.size()-1, (T)(Object)(ValueLast.floatValue()+nRestNum.floatValue()));
            }
        }

        return resultArray;
    }

}
