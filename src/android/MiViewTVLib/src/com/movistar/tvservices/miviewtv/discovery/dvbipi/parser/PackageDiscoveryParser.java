package com.movistar.tvservices.miviewtv.discovery.dvbipi.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by Sergio Moreno Mozota on 30/11/15.
 *
 * Copyright Telefónica de España SAU 2015
 */
public class PackageDiscoveryParser extends ServiceDiscoveryParser {
    private String TAG = "PackageDiscovery";

    private static final PackageDiscoveryParser instance = new PackageDiscoveryParser();

    protected PackageDiscoveryParser() {}

    public static PackageDiscoveryParser getInstance() { return instance; }

    @Override
    protected String getTAG() {
        return TAG;
    }

    /*
     * <ServiceDiscovery xmlns="urn:dvb:ipisdns:2006">
     *    <PackageDiscovery DomainName="imagenio.es" Version="106">
     *       <Package Id="1">
     *          <PackageName Language="ENG">UTX00</PackageName>
     *          <Service>
     *             <TextualID ServiceName="597" />
     *             <LogicalChannelNumber>0</LogicalChannelNumber>
     *          </Service>
     *          <Service>
     *             <TextualID ServiceName="1" />
     *             <LogicalChannelNumber>1</LogicalChannelNumber>
     *          </Service>
     */

    @Override
    protected Object readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        PackageDiscoveryData packageDiscoveryData = new PackageDiscoveryData();
        int depth = parser.getDepth();

        while (parser.next() != XmlPullParser.END_TAG || parser.getDepth() > depth) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String firstlevelName = parser.getName();
            if (null != firstlevelName && firstlevelName.equals("Package")) {
                packageDiscoveryData.addPackage(readPackage(parser));
            }  else {
                skip(parser);
            }
        }

        return packageDiscoveryData;
    }

    private PackageDiscoveryData.Package readPackage(XmlPullParser parser) throws XmlPullParserException, IOException {
        PackageDiscoveryData.Package packageData = new PackageDiscoveryData.Package();
        int depth = parser.getDepth();

        packageData.setPackageId(parser.getAttributeValue(ns,"Id"));

        while (parser.next() != XmlPullParser.END_TAG || parser.getDepth() > depth) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String secondlevelName = parser.getName();
            if (null != secondlevelName && secondlevelName.equals("PackageName")) {
                parser.next();
                packageData.setPackageName(parser.getText());
            } else if (null != secondlevelName && secondlevelName.equals("Service")) {
                readService(parser, packageData);
            } else {
                skip(parser);
            }
        }

        return packageData;
    }

    private void readService(XmlPullParser parser, PackageDiscoveryData.Package packageData) throws XmlPullParserException, IOException {
        String textualId = null;
        int logicalChannelNumber = -1;
        int depth = parser.getDepth();

        while (parser.next() != XmlPullParser.END_TAG || parser.getDepth() > depth) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String thirdlevelName = parser.getName();
            if (null != thirdlevelName && thirdlevelName.equals("TextualID")) {
                textualId = parser.getAttributeValue(ns, "ServiceName");
            } else if (null != thirdlevelName && thirdlevelName.equals("LogicalChannelNumber")) {
                parser.next();
                logicalChannelNumber = Integer.valueOf(parser.getText());
            } else {
                skip(parser);
            }
        }

        if (logicalChannelNumber != -1)
            packageData.addService(textualId, logicalChannelNumber);
    }
}
