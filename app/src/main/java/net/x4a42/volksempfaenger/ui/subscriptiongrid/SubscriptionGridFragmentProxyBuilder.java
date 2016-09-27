package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import net.x4a42.volksempfaenger.ui.addsubscription.AddSubscriptionActivityIntentProvider;
import net.x4a42.volksempfaenger.ui.addsubscription.AddSubscriptionActivityIntentProviderBuilder;

public class SubscriptionGridFragmentProxyBuilder
{
    public SubscriptionGridFragmentProxy build(SubscriptionGridFragment fragment)
    {
        GridManager gridManager
                = new GridManagerBuilder().build(fragment.getActivity());

        OptionsMenuManager menuManager = new OptionsMenuManager();

        AddSubscriptionActivityIntentProvider addSubscriptionIntentProvider
                = new AddSubscriptionActivityIntentProviderBuilder().build(fragment.getActivity());

        SubscriptionGridFragmentProxy proxy
                = new SubscriptionGridFragmentProxy(fragment,
                                                    gridManager,
                                                    menuManager,
                                                    addSubscriptionIntentProvider);

        menuManager.setListener(proxy);

        return proxy;
    }
}
