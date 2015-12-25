package net.gangelov.transporter.network.protocol;

public interface IConnectionHandler extends Runnable {
    public void close();

    PacketReader getReader();
    PacketWriter getWriter();
}
