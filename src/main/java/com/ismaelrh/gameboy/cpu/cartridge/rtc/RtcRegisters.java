package com.ismaelrh.gameboy.cpu.cartridge.rtc;

public class RtcRegisters {

    private RealTimeClock rtClock;

    private FixedClock fixedClock;

    private byte prevLatchValue = (byte) 0xFF;
    private boolean isLatched = false;


    public RtcRegisters() {
        this.rtClock = new RealTimeClock();
    }

    public RtcRegisters(RealTimeClock clock) {
        this.rtClock = clock;
    }

    public void writeLatch(byte data) {
        if (prevLatchValue == 0x00 && data == 0x01) {
            isLatched = !isLatched;
            if (isLatched) {
                this.fixedClock = rtClock.getFixedClock();
            }
        }
        prevLatchValue = data;

    }

    public void setHalt(boolean halt) {
        rtClock.setHalt(halt);
    }

    //Read register from 0x08 to 0x0C
    public byte readRegister(byte register) {
        switch (register) {
            case (byte) 0x08:
                return readSeconds();
            case (byte) 0x09:
                return readMinutes();
            case (byte) 0x0A:
                return readHours();
            case (byte) 0x0B:
                return readDayLow();
            case (byte) 0x0C:
                return readDayHigh();
            default:
                System.err.println("Error, reading register " + register + " of RTC");
                return (byte) 0x00;
        }
    }

    public void writeRegister(byte register, byte data) {
        switch (register) {
            case (byte) 0x08:
                writeSeconds(data);
                break;
            case (byte) 0x09:
                writeMinutes(data);
                break;
            case (byte) 0x0A:
                writeHours(data);
                break;
            case (byte) 0x0B:
                writeDayLow(data);
                break;
            case (byte) 0x0C:
                writeDayHigh(data);
                break;
            default:
                System.err.println("Error, writing register " + register + " of RTC");
        }
    }

    public byte readSeconds() {
        return (byte) (getClock().getSeconds() & 0xFF);
    }

    public void writeSeconds(byte data) {
        data = (byte) (data & 0x3F); //Clear unused bytes
        rtClock.setSeconds(data);
    }

    public byte readMinutes() {
        return (byte) (getClock().getMinutes() & 0xFF);
    }

    public void writeMinutes(byte data) {
        data = (byte) (data & 0x3F); //Clear unused bytes
        rtClock.setMinutes(data);
    }

    public byte readHours() {
        return (byte) (getClock().getHours() & 0xFF);
    }


    public void writeHours(byte data) {
        data = (byte) (data & 0x1F); //Clear unused bytes
        rtClock.setHours(data);

    }

    public byte readDayLow() {
        return (byte) (getClock().getDays() & 0xFF);
    }

    public void writeDayLow(byte data) {
        int currentDay = rtClock.getDays();
        rtClock.setDays(((currentDay & 0xFF00) | data) & 0xFF);
    }

    public byte readDayHigh() {

        byte dayBit = (byte) ((getClock().getDays() >> 8) & 0x01);   //Bit 0
        byte haltBit = rtClock.getHalted() ? (byte) 0x40 : 0x00; //Bit 6
        byte dayCarryBit = (byte) (getClock().getDayOverflow() ? 0x80 : 0);  //Bit 7

        return (byte) (dayBit | haltBit | dayCarryBit);
    }

    public void writeDayHigh(byte data) {
        boolean dayOverflow = (data & 0x80) != 0x00;
        boolean halt = (data & 0x40) != 0x00;
        int upperDay = ((data & 0x01) << 8);

        rtClock.setDayOverflow(dayOverflow);

        int currentDay = rtClock.getDays();
        rtClock.setDays((currentDay & 0x00FF) | (upperDay));
        setHalt(halt);
    }

    private Clock getClock() {
        if (isLatched) {
            return fixedClock;
        } else {
            return rtClock;
        }
    }

}
