/*
    This file is part of Peers.

    Peers is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    Peers is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
    
    Copyright 2007 Yohann Martineau 
*/

package net.sourceforge.peers.sip;

import static net.sourceforge.peers.sip.RFC3261.HDR_VIA;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.sourceforge.peers.sip.syntaxencoding.SipHeaderFieldMultiValue;
import net.sourceforge.peers.sip.syntaxencoding.SipHeaderFieldName;
import net.sourceforge.peers.sip.syntaxencoding.SipHeaderFieldValue;
import net.sourceforge.peers.sip.syntaxencoding.SipHeaders;
import net.sourceforge.peers.sip.transport.SipMessage;


public class Utils {

    private static Utils INSTANCE;
    
    public static Utils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Utils();
        }
        return INSTANCE;
    }
    
    private InetAddress myAddress;
    private int cseqCounter;

    private Utils() {
        super();
        

        try {
            //TODO make it configurable
            myAddress = InetAddress.getByName("192.168.2.2");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
//        try {
//            boolean found = false;
//            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
//            while (e.hasMoreElements() && !found) {
//                NetworkInterface networkInterface = e.nextElement();
//                // System.out.println(networkInterface.getDisplayName());
//                Enumeration<InetAddress> f = networkInterface.getInetAddresses();
//                while (f.hasMoreElements() && !found) {
//                    InetAddress inetAddress = f.nextElement();
//                    if (inetAddress.isSiteLocalAddress()) {
//                        this.myAddress = inetAddress;
//                        found = true;
//                    }
//                }
//            }
//        } catch (SocketException e) {
//            e.printStackTrace();
//        }
        cseqCounter = 0;
    }

    public InetAddress getMyAddress() {
        return myAddress;
    }
    
    public SipHeaderFieldValue getTopVia(SipMessage sipMessage) {
        SipHeaders sipHeaders = sipMessage.getSipHeaders();
        SipHeaderFieldName viaName = new SipHeaderFieldName(HDR_VIA);
        SipHeaderFieldValue via = sipHeaders.get(viaName);
        if (via instanceof SipHeaderFieldMultiValue) {
            via = ((SipHeaderFieldMultiValue)via).getValues().get(0);
        }
        return via;
    }
    
    public String generateTag() {
        return randomString(8);
    }
    
    public String generateCallID() {
        //TODO make a hash using current time millis, public ip @, private @, and a random string
        StringBuffer buf = new StringBuffer();
        buf.append(randomString(8));
        buf.append('-');
        buf.append(String.valueOf(System.currentTimeMillis()));
        buf.append('@');
        buf.append(myAddress.getHostName());
        return buf.toString();
    }
    
    public String generateCSeq(String method) {
        StringBuffer buf = new StringBuffer();
        buf.append(cseqCounter++);
        buf.append(' ');
        buf.append(method);
        return buf.toString();
    }
    
    public String generateBranchId() {
        StringBuffer buf = new StringBuffer();
        buf.append(RFC3261.BRANCHID_MAGIC_COOKIE);
        //TODO must be unique across space and time...
        buf.append(randomString(9));
        return buf.toString();
    }
    
    public String randomString(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz" +
                       "ABCDEFGHIFKLMNOPRSTUVWXYZ" +
                       "0123456789";
        StringBuffer buf = new StringBuffer(length);
        for (int i = 0; i < length; ++i) {
            int pos = (int)Math.round(Math.random() * (chars.length() - 1));
            buf.append(chars.charAt(pos));
        }
        return buf.toString();
    }
    
    public void copyHeader(SipMessage src, SipMessage dst, String name) {
        SipHeaderFieldName sipHeaderFieldName = new SipHeaderFieldName(name);
        SipHeaderFieldValue sipHeaderFieldValue = src.getSipHeaders().get(sipHeaderFieldName);
        dst.getSipHeaders().add(sipHeaderFieldName, sipHeaderFieldValue);
    }
    
    /**
     * adds Max-Forwards Supported and Require headers
     * @param headers
     */
    public void addCommonHeaders(SipHeaders headers) {
        //Max-Forwards
        
        headers.add(new SipHeaderFieldName(RFC3261.HDR_MAXFORWARDS),
                new SipHeaderFieldValue(
                        String.valueOf(RFC3261.DEFAULT_MAXFORWARDS)));
        
        //TODO Supported and Require
    }
    
    
    public String getUserPart(String sipUri) {
        int start = sipUri.indexOf(RFC3261.SCHEME_SEPARATOR);
        int end = sipUri.indexOf(RFC3261.AT);
        return sipUri.substring(start + 1, end);
    }

}