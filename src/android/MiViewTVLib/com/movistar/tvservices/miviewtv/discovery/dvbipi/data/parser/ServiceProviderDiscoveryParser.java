package com.movistar.tvservices.miviewtv.discovery.dvbipi.data.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by Sergio Moreno Mozota on 30/11/15.
 *
 * Copyright Telefónica de España SAU 2015
 */
public class ServiceProviderDiscoveryParser extends ServiceDiscoveryParser {
    private static final String TAG = "ServiceProviderDiscovery";
    private static final ServiceProviderDiscoveryParser instance = new ServiceProviderDiscoveryParser();

    protected ServiceProviderDiscoveryParser() {}

    public ServiceProviderDiscoveryParser getInstance() { return instance; }

    @Override
    protected String getTAG() {
        return TAG;
    }

    /*
     * <ServiceDiscovery xmlns="urn:dvb:ipisdns:2006" xmlns:mpeg7="urn:tva:mpeg7:2005" xmlns:tva="urn:tva:metadata:2005" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="urn:dvb:ipisdns:2006 C:\DVB_IPI\sdns.xsd">
     *    <ServiceProviderDiscovery Version="252">
     *       <ServiceProvider DomainName="DEM_1.imagenio.es" Version="31">
     *          <Offering>
     *             <Push Address="239.0.2.140" Port="3937" />
     *          </Offering>
     *       </ServiceProvider>
     */

    @Override
    protected Object readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        ServiceProviderDiscoveryData serviceProviderDiscoveryData = new ServiceProviderDiscoveryData();
        int depth = parser.getDepth();

        while (parser.next() != XmlPullParser.END_TAG || parser.getDepth() > depth) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String firstlevelName = parser.getName();
            if (null != firstlevelName && firstlevelName.equals("ServiceProvider")) {
                ServiceProviderDiscoveryData.ServiceProvider serviceProvider = new ServiceProviderDiscoveryData.ServiceProvider();
                serviceProvider.setDomainName(parser.getAttributeValue(null, "DomainName"));
                serviceProvider.setVersion(Integer.parseInt(parser.getAttributeValue(null, "Version")));

                readOffering(parser, serviceProvider);

                serviceProviderDiscoveryData.addServiceProvider(serviceProvider);

            } else {
                skip(parser);
            }
        }

        return serviceProviderDiscoveryData;
    }

    private void readOffering(XmlPullParser parser, ServiceProviderDiscoveryData.ServiceProvider serviceProvider) throws XmlPullParserException, IOException {
        int depth = parser.getDepth();

        while (parser.next() != XmlPullParser.END_TAG || parser.getDepth() > depth) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String secondlevelName = parser.getName();
            if (null != secondlevelName && secondlevelName.equals("Offering")) {
                readPush(parser, serviceProvider);
            }  else {
                skip(parser);
            }
        }
    }

    private void readPush(XmlPullParser parser, ServiceProviderDiscoveryData.ServiceProvider serviceProvider) throws XmlPullParserException, IOException {
        int depth = parser.getDepth();

        while (parser.next() != XmlPullParser.END_TAG || parser.getDepth() > depth) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String thirdlevelName = parser.getName();
            if (null != thirdlevelName && thirdlevelName.equals("Push")) {
                serviceProvider.setAddress(parser.getAttributeValue(null, "Address"));
                serviceProvider.setPort(Integer.parseInt(parser.getAttributeValue(null, "Port")));
            }  else {
                skip(parser);
            }
        }
    }
}
