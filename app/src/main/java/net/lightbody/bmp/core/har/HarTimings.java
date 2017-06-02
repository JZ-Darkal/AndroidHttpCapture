package net.lightbody.bmp.core.har;

import java.util.concurrent.TimeUnit;

public class HarTimings {
    // optional values are initialized to -1, which indicates they do not apply to the current request, according to the HAR spec
    private volatile long blockedNanos = -1;
    private volatile long dnsNanos = -1;
    private volatile long connectNanos = -1;
    private volatile long sendNanos;
    private volatile long waitNanos;
    private volatile long receiveNanos;
    private volatile long sslNanos = -1;
    private volatile String comment = "";

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    // the following getters and setters take a TimeUnit parameter, to allow finer precision control when no marshalling to JSON
    public long getBlocked(TimeUnit timeUnit) {
        if (blockedNanos == -1) {
            return -1;
        } else {
            return timeUnit.convert(blockedNanos, TimeUnit.NANOSECONDS);
        }
    }

    public void setBlocked(long blocked, TimeUnit timeUnit) {
        if (blocked == -1) {
            this.blockedNanos = -1;
        } else {
            this.blockedNanos = TimeUnit.NANOSECONDS.convert(blocked, timeUnit);
        }
    }

    public long getDns(TimeUnit timeUnit) {
        if (dnsNanos == -1) {
            return -1;
        } else {
            return timeUnit.convert(dnsNanos, TimeUnit.NANOSECONDS);
        }
    }

    public void setDns(long dns, TimeUnit timeUnit) {
        if (dns == -1) {
            this.dnsNanos = -1;
        } else{
            this.dnsNanos = TimeUnit.NANOSECONDS.convert(dns, timeUnit);
        }
    }

    public long getConnect(TimeUnit timeUnit) {
        if (connectNanos == -1) {
            return -1;
        } else {
            return timeUnit.convert(connectNanos, TimeUnit.NANOSECONDS);
        }
    }

    public void setConnect(long connect, TimeUnit timeUnit) {
        if (connect == -1) {
            this.connectNanos = -1;
        } else {
            this.connectNanos = TimeUnit.NANOSECONDS.convert(connect, timeUnit);
        }
    }

    /*
        According to the HAR spec:
            The send, wait and receive timings are not optional and must have non-negative values.
     */
    public long getSend(TimeUnit timeUnit) {
        return timeUnit.convert(sendNanos, TimeUnit.NANOSECONDS);
    }

    public void setSend(long send, TimeUnit timeUnit) {
        this.sendNanos = TimeUnit.NANOSECONDS.convert(send, timeUnit);
    }

    public long getWait(TimeUnit timeUnit) {
        return timeUnit.convert(waitNanos, TimeUnit.NANOSECONDS);
    }

    public void setWait(long wait, TimeUnit timeUnit) {
        this.waitNanos = TimeUnit.NANOSECONDS.convert(wait, timeUnit);
    }

    public long getReceive(TimeUnit timeUnit) {
        return timeUnit.convert(receiveNanos, TimeUnit.NANOSECONDS);
    }

    public void setReceive(long receive, TimeUnit timeUnit) {
        this.receiveNanos = TimeUnit.NANOSECONDS.convert(receive, timeUnit);
    }

    public long getSsl(TimeUnit timeUnit) {
        if (sslNanos == -1) {
            return -1;
        } else {
            return timeUnit.convert(sslNanos, TimeUnit.NANOSECONDS);
        }
    }

    public void setSsl(long ssl, TimeUnit timeUnit) {
        if (ssl == -1) {
            this.sslNanos = -1;
        } else {
            this.sslNanos = TimeUnit.NANOSECONDS.convert(ssl, timeUnit);
        }
    }

    // the following getters and setters assume TimeUnit.MILLISECOND precision. this allows jackson to generate ms values (in accordance
    // with the HAR spec), and also preserves compatibility with the legacy methods. optional methods are also declared as Long instead of
    // long (even though they always have values), to preserve compatibility. in general, the getters/setters which take TimeUnits
    // should always be preferred.
    public Long getBlocked() {
        return getBlocked(TimeUnit.MILLISECONDS);
    }

    public void setBlocked(long blocked) {
        setBlocked(blocked, TimeUnit.MILLISECONDS);
    }

    public Long getDns() {
        return getDns(TimeUnit.MILLISECONDS);
    }

    public void setDns(long dns) {
        setDns(dns, TimeUnit.MILLISECONDS);
    }

    public Long getConnect() {
        return getConnect(TimeUnit.MILLISECONDS);
    }

    public void setConnect(long connect) {
        setConnect(connect, TimeUnit.MILLISECONDS);
    }

    public long getSend() {
        return getSend(TimeUnit.MILLISECONDS);
    }

    public void setSend(long send) {
        setSend(send, TimeUnit.MILLISECONDS);
    }

    public long getWait() {
        return getWait(TimeUnit.MILLISECONDS);
    }

    public void setWait(long wait) {
        setWait(wait, TimeUnit.MILLISECONDS);
    }

    public long getReceive() {
        return getReceive(TimeUnit.MILLISECONDS);
    }

    public void setReceive(long receive) {
        setReceive(receive, TimeUnit.MILLISECONDS);
    }

    public Long getSsl() {
        return getSsl(TimeUnit.MILLISECONDS);
    }

    public void setSsl(long ssl) {
        setSsl(ssl, TimeUnit.MILLISECONDS);
    }

}
