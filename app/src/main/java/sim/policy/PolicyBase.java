package sim.policy;

import sim.app.Controller;

//政策的基类
public abstract class PolicyBase {

    private String m_strPolicyName = "";
    public enum PolicyState
    {
        PS_NotUsable,           //不可用(可用条件不满足)
        PS_NotActivate,         //可用，但未被启用
        PS_Activate             //已启用
    }

    private PolicyState m_PolicyState = PolicyState.PS_NotUsable;

    public String m_strUsableDate = "";

    public PolicyBase(String strPolicyName)
    {
        m_strPolicyName = strPolicyName;
    }

    public String getPolicyName()
    {
        return m_strPolicyName;
    }

    public void checkUsable()
    {
        if (isUsableByDate())
        {
            if (m_PolicyState == PolicyState.PS_NotUsable)
            {
                setPolicyState(PolicyState.PS_NotActivate);
            }
        }
    }

    public boolean isUsableByDate()
    {
        if (m_strUsableDate.isEmpty())
        {
            return false;
        }
        return Controller.getInstance().isDateReached(m_strUsableDate);
    }

    public boolean isActive()
    {
        if (m_PolicyState == PolicyState.PS_Activate)
        {
            return true;
        }
        return false;
    }

    public void setPolicyState(PolicyState NewPolicyState)
    {
        if(NewPolicyState == m_PolicyState)
        {
            return;
        }

        PolicyState OldPolicyState = m_PolicyState;
        m_PolicyState = NewPolicyState;

        if (OldPolicyState == PolicyState.PS_NotActivate
                && NewPolicyState == PolicyState.PS_Activate)
        {
            onActivated();
        }
        else if (OldPolicyState == PolicyState.PS_Activate
                && (NewPolicyState == PolicyState.PS_NotActivate
                    || NewPolicyState == PolicyState.PS_NotUsable))
        {
            onUnActivated();
        }
        else if (OldPolicyState == PolicyState.PS_NotUsable
                && NewPolicyState == PolicyState.PS_NotActivate)
        {
            onUsable();
        }
    }

    //从PS_NotUsable变成PS_NotActivate的时候调用
    public abstract void onUsable();

    //从PS_NotActivate变成PS_Activate的时候调用
    public abstract void onActivated();

    //从PS_Activate变成PS_NotActivate或PS_NotUsable的时候调用
    public abstract void onUnActivated();

}
