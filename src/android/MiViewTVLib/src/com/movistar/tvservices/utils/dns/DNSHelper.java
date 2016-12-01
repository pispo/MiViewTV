package com.movistar.tvservices.utils.dns;

import org.xbill.DNS.ARecord;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;

public class DNSHelper {
    private static final String LOG_TAG = DNSHelper.class.getSimpleName();

    private static final String DEFAULT_DNS_ADDR = "172.26.23.3";
    
    public static String resolve(String domain) {
        String address = null;

        try {

            Lookup lookup = new Lookup(domain);
            Resolver resolver = new SimpleResolver(DEFAULT_DNS_ADDR);
            lookup.setResolver(resolver);

            Record recs[] = lookup.run();
            if (recs == null) {
                return null;
            }

            for (Record rec : recs) {
                address = (((ARecord) rec).getAddress()).getHostAddress();
                break;
            }

        } catch (TextParseException e) {
            address = null;

        } catch (NullPointerException e) {
            address = null;

        } catch (UnknownHostException e) {
            e.printStackTrace();
            address = null;
        }

        return address;
    }
}
