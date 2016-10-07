package net.x4a42.volksempfaenger.ui.subscriptiongrid;

class SubscriptionGridFragmentProxyBuilder
{
    public SubscriptionGridFragmentProxy build(SubscriptionGridFragment fragment)
    {
        GridManager gridManager = new GridManagerBuilder().build(fragment.getActivity());
        return new SubscriptionGridFragmentProxy(fragment, gridManager);
    }
}
