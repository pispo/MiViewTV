package com.movistar.tvservices.miviewtv.discovery.dvbipi.parser;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.movistar.tvservices.miviewtv.discovery.dvbipi.data.BroadcastDiscoveryData;

/**
 * Created by Sergio Moreno Mozota on 30/11/15.
 *
 * Copyright Telefónica de España SAU 2015
 */
public class BroadcastDiscoveryParser extends ServiceDiscoveryParser {
    private static final String TAG = "BroadcastDiscovery";
    private static final BroadcastDiscoveryParser instance = new BroadcastDiscoveryParser();

    private static Pattern genrePattern = Pattern.compile(".*:([^:\\.]*)(\\.([^:]*))?$");
    private static Pattern parentalRatingPattern = Pattern.compile(".*:([^:]*)$");
    
    @Override
    public static BroadcastDiscoveryData parse(InputStream in) {
        try {
            return (BroadcastDiscoveryData) super.parse(in);
        } catch (Exception e) {
            return null;
        }   
    }

    @Override
    protected static String getTAG() {
        return TAG;
    }

    /*
     *	<ServiceDiscovery xmlns="urn:dvb:ipisdns:2006" xmlns:mpeg7="urn:tva:mpeg7:2005" xmlns:urn="urn:tva:metadata:2007">
     *		<BroadcastDiscovery DomainName="imagenio.es" Version="6">
     *			<ServiceList>
     *				<SingleService>
     *					<ServiceLocation>
     *						<IPMulticastAddress Port="8208" Address="239.0.0.25"/>
     *					</ServiceLocation>
     *					<TextualIdentifier ServiceName="58" logoURI="MAY_1/imSer/58.jpg"/>
     *					<SI ServiceType="1" ServiceInfo="1">
     *						<Name Language="ENG">Telemadrid</Name>
     *						<ShortName Language="ENG">TMAD</ShortName>
     *						<Description Language="ENG">TELEMADRID</Description>
     *						<Genre href="urn:miviewtv:cs:GenreCS:2007:0">
     *							<urn:Name>ENTRETENIMIENTO</urn:Name>
     *						</Genre>
     *						<ParentalGuidance>
     *							<mpeg7:ParentalRating href="urn:dvb:metadata:cs:ParentalGuidanceCS:2007:1">
     *								<mpeg7:Name>No rating available</mpeg7:Name>
     *							</mpeg7:ParentalRating>
     *						</ParentalGuidance>
     *					</SI>
     *				</SingleService>
     *	...
     *
     */

    @Override
    protected static Object readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        BroadcastDiscoveryData broadcastDiscoveryData = new BroadcastDiscoveryData();
        int depth = parser.getDepth();

        while (parser.next() != XmlPullParser.END_TAG || parser.getDepth() > depth) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String firstlevelName = parser.getName();
            if (null != firstlevelName && firstlevelName.equals("ServiceList")) {
                readServiceList(parser, broadcastDiscoveryData);
            }  else {
                skip(parser);
            }
        }

        return broadcastDiscoveryData;
    }

    private static void readServiceList(XmlPullParser parser, BroadcastDiscoveryData broadcastDiscoveryData) throws XmlPullParserException, IOException {
        int depth = parser.getDepth();

        while (parser.next() != XmlPullParser.END_TAG || parser.getDepth() > depth) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String secondlevelName = parser.getName();
            if (null != secondlevelName && secondlevelName.equals("SingleService")) {
                broadcastDiscoveryData.addService(readSingleService(parser));
            }  else {
                skip(parser);
            }
        }
    }

    private static BroadcastDiscoveryData.Service readSingleService(XmlPullParser parser) throws XmlPullParserException, IOException {
        BroadcastDiscoveryData.Service service = new BroadcastDiscoveryData.Service();
        int depth = parser.getDepth();

        while (parser.next() != XmlPullParser.END_TAG || parser.getDepth() > depth) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String thirdlevelName = parser.getName();
            if (null != thirdlevelName && thirdlevelName.equals("ServiceLocation")) {
                readServiceLocation(parser, service);
            } else if (null != thirdlevelName && thirdlevelName.equals("TextualIdentifier")) {
                service.setServiceName(parser.getAttributeValue(ns, "ServiceName"));
                service.setLogoURI(parser.getAttributeValue(ns, "logoURI"));
            } else if (null != thirdlevelName && thirdlevelName.equals("SI")) {
                String att = parser.getAttributeValue(ns, "ServiceType");
                if (att.equals("22") || att.equals("25") || att.equals("153")) {
                    service.setHD(true);
                } else {
                    service.setHD(false);
                }

                service.setFlags(Integer.valueOf(parser.getAttributeValue(ns, "ServiceInfo")));
                readSI(parser, service);

            } else if (null != thirdlevelName && thirdlevelName.equals("AudioAttributes")) {
                readAudioAttributes(parser, service);
            } else {
                skip(parser);
            }
        }

        return service;
    }

    private static void readServiceLocation(XmlPullParser parser, BroadcastDiscoveryData.Service service) throws XmlPullParserException, IOException {
        int depth = parser.getDepth();

        while (parser.next() != XmlPullParser.END_TAG || parser.getDepth() > depth) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String fourthlevelName = parser.getName();
            if (fourthlevelName.equals("IPMulticastAddress")) {
                service.setAddress(parser.getAttributeValue(ns, "Address"));
                service.setPort(Integer.valueOf(parser.getAttributeValue(ns, "Port")));
            } else {
                skip(parser);
            }
        }
    }

    private static void readSI(XmlPullParser parser, BroadcastDiscoveryData.Service service) throws XmlPullParserException, IOException {
        int depth = parser.getDepth();

        while (parser.next() != XmlPullParser.END_TAG || parser.getDepth() > depth) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String fourhtlevelName = parser.getName();
            if (null != fourhtlevelName && fourhtlevelName.equals("Name")) {
                service.setName(parser.nextText());
            } else if (null != fourhtlevelName && fourhtlevelName.equals("ShortName")) {
                service.setShortName(parser.nextText());
            } else if (null != fourhtlevelName && fourhtlevelName.equals("Description")) {
                service.setDescription(parser.nextText());
            } else if (null != fourhtlevelName && fourhtlevelName.equals("Genre")) {
                readGenre(parser, service);
            } else if (null != fourhtlevelName && fourhtlevelName.equals("ParentalGuidance")) {
                readParentalGuidance(parser, service);
            } else if (null != fourhtlevelName && fourhtlevelName.equals("ReplacementService")) {
                readReplacementService(parser, service);
            } else {
                skip(parser);
            }
        }
    }

    private static void readAudioAttributes(XmlPullParser parser, BroadcastDiscoveryData.Service service) throws XmlPullParserException, IOException {
        int depth = parser.getDepth();
        while (parser.next() != XmlPullParser.END_TAG || parser.getDepth() > depth) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            
            String fourhtlevelName = parser.getName();
            if (null != fourhtlevelName && fourhtlevelName.equals("urn:MixType")) {
                readAudioMixType(parser, service);
            } else {
                skip(parser);
            }
        }
    }

    private static void readAudioMixType(XmlPullParser parser, BroadcastDiscoveryData.Service service) throws XmlPullParserException, IOException {
        Matcher matcher;
        int depth = parser.getDepth();

        while (parser.next() != XmlPullParser.END_TAG || parser.getDepth() > depth) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            
            String fifthlevelName = parser.getName();
            if (null != fifthlevelName && fifthlevelName.equals("urn:Name")) { // with no namespaces we have to pass the raw name
                service.setDolby(parser.nextText().equals("D") ? true : false);
            }  else {
                skip(parser);
            }
        }
    }

    private static void readGenre(XmlPullParser parser, BroadcastDiscoveryData.Service service) {
        String genre = parser.getAttributeValue(ns, "href");
        Matcher matcher = genrePattern.matcher(genre);

        if (matcher.matches()) {
            if (null != matcher.group(1)) {
                service.setGenre(Integer.valueOf(matcher.group(1)));
            }
            
            if (null != matcher.group(3)) {
                service.setSubgenre(Integer.valueOf(matcher.group(3)));
            }
        }
    }

    private static void readParentalGuidance(XmlPullParser parser, BroadcastDiscoveryData.Service service) throws XmlPullParserException, IOException {
        Matcher matcher;
        int depth = parser.getDepth();

        while (parser.next() != XmlPullParser.END_TAG || parser.getDepth() > depth) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            
            String fifthlevelName = parser.getName();
            if (null != fifthlevelName && fifthlevelName.equals("mpeg7:ParentalRating")) { // with no namespaces we have to pass the raw name
                String parentalRating = parser.getAttributeValue(ns, "href");
                matcher = parentalRatingPattern.matcher(parentalRating);
                if (matcher.matches()) {
                    if (null != matcher.group(1)) {
                        service.setParentalRating(Integer.valueOf(matcher.group(1)));
                    }
                }
            }  else {
                skip(parser);
            }
        }
    }

    private static void readReplacementService(XmlPullParser parser, BroadcastDiscoveryData.Service service) throws XmlPullParserException, IOException {
        int depth = parser.getDepth();
        while (parser.next() != XmlPullParser.END_TAG || parser.getDepth() > depth) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            
            String fifthlevelName = parser.getName();
            if (null != fifthlevelName && fifthlevelName.equals("TextualIdentifier")) {
                service.setReplacementService(parser.getAttributeValue(ns, "ServiceName"));
            }  else {
                skip(parser);
            }
        }
    }

}
