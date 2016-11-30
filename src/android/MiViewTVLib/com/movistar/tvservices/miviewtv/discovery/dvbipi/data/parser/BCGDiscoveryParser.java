package com.movistar.tvservices.discovery.dvbipi.data.parser;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Sergio Moreno Mozota on 30/11/15.
 *
 * Copyright Telefónica de España SAU 2015
 */
public class BCGDiscoveryParser extends ServiceDiscoveryParser {
    private static final String TAG = "BCGDiscovery";
    private static final BCGDiscoveryParser instance = new BCGDiscoveryParser();

    protected BCGDiscoveryParser() {}

    public BCGDiscoveryParser getInstance() { return instance; }

    @Override
    protected String getTAG() {
        return TAG;
    }

    /*
     * <ServiceDiscovery xmlns="urn:dvb:ipisdns:2006" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
     *    <BCGDiscovery DomainName="DEM_19.imagenio.es" Version="186">
     *       <BCG Id="p_f">
     *          <Name Language="eng">MiViewTV Present/Following</Name>
     *             <TransportMode>
     *                <DVBSTP Port="3937" Address="239.0.2.155"/>
     *             </TransportMode>
     *          <TargetProvider>DEM_19.imagenio.es</TargetProvider>
     *       </BCG>
     *       <BCG Id="EPG">
     *          <Name Language="eng">MiViewTV ProgramGuide</Name>
     *          <TransportMode>
     *             <DVBBINSTP Source="EPG_0_BIN.imagenio.es" Port="3937" Address="239.0.2.130">
     *                <PayloadId Id="0xF1">
     *                   <Segment ID="0x30c5" Version="0"/>
     *                   <Segment ID="0x330c" Version="0"/>
     *                   ...
     *                   <Segment ID="0x3cab" Version="0"/>
     *                </PayloadId>
     *                <PayloadId Id="0xF2"/>
     *             </DVBBINSTP>
     *             <DVBBINSTP Source="EPG_1_BIN.imagenio.es" Port="3937" Address="239.0.2.131">
     *                <PayloadId Id="0xF1">
     *                   <Segment ID="0x4b1e" Version="2"/>
     *                   ...
     *                   <Segment ID="0x7cab" Version="0"/>
     *                </PayloadId>
     *                <PayloadId Id="0xF2"/>
     *             </DVBBINSTP>
     *          </TransportMode>
     *       </BCG>
     *    </BCGDiscovery>
     * </ServiceDiscovery>
     */

    @Override
    protected Object readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        BCGDiscoveryData bcgDiscoveryData = new BCGDiscoveryData();
        int depth = parser.getDepth();

        while (parser.next() != XmlPullParser.END_TAG || parser.getDepth() > depth) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String firstlevelName = parser.getName();
            if (null != firstlevelName && firstlevelName.equals("BCG")) {
                bcgDiscoveryData.addBroadContentGuide(readBCG(parser));
            } else {
                skip(parser);
            }
        }

        return bcgDiscoveryData;
    }

    private BCGDiscoveryData.BCG readBCG(XmlPullParser parser) throws XmlPullParserException, IOException {
        BCGDiscoveryData.BCG bcg = new BCGDiscoveryData.BCG();
        bcg.setId(parser.getAttributeValue(ns,"Id"));
        int depth = parser.getDepth();

        while (parser.next() != XmlPullParser.END_TAG || parser.getDepth() > depth) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String secondlevelName = parser.getName();
            if (null != secondlevelName && secondlevelName.equals("Name")) {
                parser.next();
                bcg.setName(parser.getText());
            } else if (null != secondlevelName && secondlevelName.equals("TransportMode")) {
                readTransport(parser, bcg);
            } else {
                skip(parser);
            }
        }

        return bcg;
    }

    private BCGDiscoveryData.TransportMode readTransport(XmlPullParser parser, BCGDiscoveryData.BCG bcg) throws XmlPullParserException, IOException {
        BCGDiscoveryData.TransportMode transportMode = new BCGDiscoveryData.TransportMode();
        int depth = parser.getDepth();

        while (parser.next() != XmlPullParser.END_TAG || parser.getDepth() > depth) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String thirdlevelName = parser.getName();
            if (null != thirdlevelName && thirdlevelName.equals("DVBBINSTP")) {
                transportMode.setSource(parser.getAttributeValue(ns, "Source"));
                transportMode.setAddress(parser.getAttributeValue(ns, "Address"));
                transportMode.setPort(Integer.valueOf(parser.getAttributeValue(ns, "Port")));

                readPayloadId(parser, transportMode);
                bcg.addTransportMode(transportMode);

                Log.v(TAG, "epg source= " + transportMode.getSource());

            } else {
                skip(parser);
            }
        }

        return map;
    }

    private void readPayloadId(XmlPullParser parser, BCGDiscoveryData.TransportMode transportMode) throws XmlPullParserException, IOException {
        int depth = parser.getDepth();

        while (parser.next() != XmlPullParser.END_TAG || parser.getDepth() > depth) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String fourthlevelName = parser.getName();
            if (null != fourthlevelName && fourthlevelName.equals("PayloadId")) {
                if (DvbIpiConstants.SDS_CONTENT_GUIDE_DISCOVERY == Integer.decode(parser.getAttributeValue(ns, "Id"))) {
                    readSegment(parser, transportMode);
                }
            } else {
                skip(parser);
            }
        }
    }

    private void readSegment(XmlPullParser parser, BCGDiscoveryData.TransportMode transportMode) throws XmlPullParserException, IOException {
        int depth = parser.getDepth();

        while (parser.next() != XmlPullParser.END_TAG || parser.getDepth() > depth) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String fifthlevelName = parser.getName();
            if (null != fifthlevelName && fifthlevelName.equals("Segment")) {
                transportMode.addSegment(
                        Integer.decode(parser.getAttributeValue(ns, "ID")),
                        Integer.valueOf(parser.getAttributeValue(ns, "Version")));
            } else {
                skip(parser);
            }
        }
    }
}
