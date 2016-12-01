package com.movistar.tvservices.miviewtv.discovery.dvbipi.parser;

import com.movistar.tvservices.utils.xml.XmlParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Sergio Moreno Mozota on 30/11/15.
 *
 * Copyright Telefónica de España SAU 2015
 */
public abstract class ServiceDiscoveryParser extends DiscoveryParser {

    private static final String TAG = "ServiceDiscovery";

    @Override
    protected Object readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        Object object = null;
        String TAG = getTAG();

        parser.require(XmlPullParser.START_TAG, ns, TAG);

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            // Starts by looking for the tag
            if (name.equals(TAG)) {
                object = readEntry(parser);
            } else {
                skip(parser);
            }
        }

        return object;
    }

    abstract protected String getTAG();

    abstract protected Object readEntry(XmlPullParser parser) throws XmlPullParserException, IOException;
}
