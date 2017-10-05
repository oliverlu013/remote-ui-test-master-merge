package net.latipay.ui.test.domain;

/**
 * @author jasonlu 4:11:39 PM
 */
public enum Currency {
                      NZD("1"), AUD("2"), CNY("5");

    private String currencyEnum;

    private Currency(String i) {
        this.currencyEnum = i;
    }

    public static Currency getCurrency(String currencyId) {
        for (Currency currency : Currency.values()) {
            if (currency.get().equals(currencyId)) {
                return currency;
            }
        }
        return null;
    }

    public String get() {
        return currencyEnum;
    }
}
