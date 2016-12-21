package com.movistar.iptv.platform.stb.sds.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.io.IOException;

import com.movistar.iptv.platform.stb.sds.data.PackageDiscoveryData;
import com.movistar.iptv.platform.stb.sds.dvbstp.DvbStpContent;

/**
 * Created by Sergio Moreno Mozota on 30/11/15.
 *
 * Copyright Telefónica de España SAU 2015
 */
public class PackageDiscoveryParser extends ServiceDiscoveryParser {
    private String TAG = "PackageDiscovery";

    protected PackageDiscoveryParser() {}

    public static PackageDiscoveryData parse(DvbStpContent content) throws DiscoveryParserException {

        try {
            return (PackageDiscoveryData) new PackageDiscoveryParser().parse(content.getByteArrayInputStream());
        } catch (Exception e) {
            throw new DiscoveryParserException("ServiceProviderDiscoveryParser: Error in parsing", e);
        }
    }
    
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
