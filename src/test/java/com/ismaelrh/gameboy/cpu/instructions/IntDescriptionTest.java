package com.ismaelrh.gameboy.cpu.instructions;

import com.ismaelrh.gameboy.cpu.instructions.implementation.Arithmetic8b;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IntDescriptionTest {

    @Test
    public void matchesAllNoPrefix() {
        InstDescription desc = new InstDescription("INST", "xx_xxx_xxx", 1, Arithmetic8b::addA_HL);

        for (int i = 0; i <= 0xFF; i++) {
            assertTrue(desc.matches(i));
        }

        for (int i = 0; i <= 0xFF; i++) {
            assertFalse(desc.matchesCB(i));
        }
    }

    @Test
    public void matchesAllPrefix() {
        InstDescription desc = new InstDescription("INST", 0xCB, "xx_xxx_xxx", 1, Arithmetic8b::addA_HL);

        for (int i = 0; i <= 0xFF; i++) {
            assertFalse(desc.matches(i));
        }

        for (int i = 0; i <= 0xFF; i++) {
            assertTrue(desc.matchesCB(i));
        }
    }

    @Test
    public void matchesSpecific() {
        InstDescription desc = new InstDescription("INST", "10_111_010", 1, Arithmetic8b::addA_HL);

        assertTrue(desc.matches(0xBA));

        for (int i = 0; i <= 0xFF; i++) {
            if(i!=0xBA){
                assertFalse(desc.matches(i));
            }
        }
    }

    @Test
    public void matchesSpecificWithoutUnderscore() {
        InstDescription desc = new InstDescription("INST", "10111010", 1, Arithmetic8b::addA_HL);

        assertTrue(desc.matches(0xBA));

        for (int i = 0; i <= 0xFF; i++) {
            if(i!=0xBA){
                assertFalse(desc.matches(i));
            }
        }
    }

    @Test
    public void matchesWithWildcards() {
        InstDescription desc = new InstDescription("INST", "1011101x", 1, Arithmetic8b::addA_HL);

        assertTrue(desc.matches(0xBA));
        assertTrue(desc.matches(0xBB));

        for (int i = 0; i <= 0xFF; i++) {
            if(i!=0xBA && i!=0xBB){
                assertFalse(desc.matches(i));
            }
        }
    }

}
