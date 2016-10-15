package cn.darkal.networkdiagnosis.Utils;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by xuzhou on 2016/8/9.
 */
public class DatatypeConverter {

        public static String doFormat( String format, Calendar cal ) throws IllegalArgumentException {
            int fidx = 0;
            int flen = format.length();
            StringBuilder buf = new StringBuilder();

            while(fidx<flen) {
                char fch = format.charAt(fidx++);

                if(fch!='%') {  // not a meta character
                    buf.append(fch);
                    continue;
                }

                // seen meta character. we don't do error check against the format
                switch (format.charAt(fidx++)) {
                    case 'Y' : // year
                        formatYear(cal, buf);
                        break;

                    case 'M' : // month
                        formatMonth(cal, buf);
                        break;

                    case 'D' : // days
                        formatDays(cal, buf);
                        break;

                    case 'h' : // hours
                        formatHours(cal, buf);
                        break;

                    case 'm' : // minutes
                        formatMinutes(cal, buf);
                        break;

                    case 's' : // parse seconds.
                        formatSeconds(cal, buf);
                        break;

                    case 'z' : // time zone
                        formatTimeZone(cal,buf);
                        break;

                    default :
                        // illegal meta character. impossible.
                        throw new InternalError();
                }
            }

            return buf.toString();
        }


        private static void formatYear(Calendar cal, StringBuilder buf) {
            int year = cal.get(Calendar.YEAR);

            String s;
            if (year <= 0) // negative value
                s = Integer.toString(1 - year);
            else // positive value
                s = Integer.toString(year);

            while (s.length() < 4)
                s = '0' + s;
            if (year <= 0)
                s = '-' + s;

            buf.append(s);
        }

        private static void formatMonth(Calendar cal, StringBuilder buf) {
            formatTwoDigits(cal.get(Calendar.MONTH)+1,buf);
        }

        private static void formatDays(Calendar cal, StringBuilder buf) {
            formatTwoDigits(cal.get(Calendar.DAY_OF_MONTH),buf);
        }

        private static void formatHours(Calendar cal, StringBuilder buf) {
            formatTwoDigits(cal.get(Calendar.HOUR_OF_DAY),buf);
        }

        private static void formatMinutes(Calendar cal, StringBuilder buf) {
            formatTwoDigits(cal.get(Calendar.MINUTE),buf);
        }

        private static void formatSeconds(Calendar cal, StringBuilder buf) {
            formatTwoDigits(cal.get(Calendar.SECOND),buf);
            if (cal.isSet(Calendar.MILLISECOND)) { // milliseconds
                int n = cal.get(Calendar.MILLISECOND);
                if(n!=0) {
                    String ms = Integer.toString(n);
                    while (ms.length() < 3)
                        ms = '0' + ms; // left 0 paddings.

                    buf.append('.');
                    buf.append(ms);
                }
            }
        }

        /** formats time zone specifier. */
        private static void formatTimeZone(Calendar cal,StringBuilder buf) {
            TimeZone tz = cal.getTimeZone();

            if (tz == null)      return;

            // otherwise print out normally.
            int offset;
            if (tz.inDaylightTime(cal.getTime())) {
                offset = tz.getRawOffset() + (tz.useDaylightTime()?3600000:0);
            } else {
                offset = tz.getRawOffset();
            }

            if(offset==0) {
                buf.append('Z');
                return;
            }

            if (offset >= 0)
                buf.append('+');
            else {
                buf.append('-');
                offset *= -1;
            }

            offset /= 60 * 1000; // offset is in milli-seconds

            formatTwoDigits(offset / 60, buf);
            buf.append(':');
            formatTwoDigits(offset % 60, buf);
        }

        /** formats Integer into two-character-wide string. */
        private static void formatTwoDigits(int n,StringBuilder buf) {
            // n is always non-negative.
            if (n < 10) buf.append('0');
            buf.append(n);
        }


    // base64 decoder
//====================================

    private static final byte[] decodeMap = initDecodeMap();
    private static final byte PADDING = 127;

    private static byte[] initDecodeMap() {
        byte[] map = new byte[128];
        int i;
        for( i=0; i<128; i++ )        map[i] = -1;

        for( i='A'; i<='Z'; i++ )    map[i] = (byte)(i-'A');
        for( i='a'; i<='z'; i++ )    map[i] = (byte)(i-'a'+26);
        for( i='0'; i<='9'; i++ )    map[i] = (byte)(i-'0'+52);
        map['+'] = 62;
        map['/'] = 63;
        map['='] = PADDING;

        return map;
    }

    private static int guessLength( String text ) {
        final int len = text.length();

        // compute the tail '=' chars
        int j=len-1;
        for(; j>=0; j-- ) {
            byte code = decodeMap[text.charAt(j)];
            if(code==PADDING)
                continue;
            if(code==-1)
                // most likely this base64 text is indented. go with the upper bound
                return text.length()/4*3;
            break;
        }

        j++;    // text.charAt(j) is now at some base64 char, so +1 to make it the size
        int padSize = len-j;
        if(padSize >2) // something is wrong with base64. be safe and go with the upper bound
            return text.length()/4*3;

        // so far this base64 looks like it's unindented tightly packed base64.
        // take a chance and create an array with the expected size
        return text.length()/4*3-padSize;
    }


    public static byte[] parseBase64Binary(String text) {
        final int buflen = guessLength(text);
        final byte[] out = new byte[buflen];
        int o=0;

        final int len = text.length();
        int i;

        final byte[] quadruplet = new byte[4];
        int q=0;

        // convert each quadruplet to three bytes.
        for( i=0; i<len; i++ ) {
            char ch = text.charAt(i);
            byte v = decodeMap[ch];

            if( v!=-1 )
                quadruplet[q++] = v;

            if(q==4) {
                // quadruplet is now filled.
                out[o++] = (byte)((quadruplet[0]<<2)|(quadruplet[1]>>4));
                if( quadruplet[2]!=PADDING )
                    out[o++] = (byte)((quadruplet[1]<<4)|(quadruplet[2]>>2));
                if( quadruplet[3]!=PADDING )
                    out[o++] = (byte)((quadruplet[2]<<6)|(quadruplet[3]));
                q=0;
            }
        }

        if(buflen==o) // speculation worked out to be OK
            return out;

        // we overestimated, so need to create a new buffer
        byte[] nb = new byte[o];
        System.arraycopy(out,0,nb,0,o);
        return nb;
    }

}
