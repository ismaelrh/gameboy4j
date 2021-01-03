package com.ismaelrh.gameboy.cpu.instructions;

import com.ismaelrh.gameboy.cpu.instructions.implementation.*;

public class InstDictionary {

    public static InstDescription[] descriptions = new InstDescription[]{

            //8-bit load commands
            new InstDescription("ld r,r", "01_xxx_xxx", 0, Load8b::loadRR),
            new InstDescription("ld r,n", "00_xxx_110", 1, Load8b::loadRImmediate),
            new InstDescription("ld r,(HL)", "01_xxx_110", 0, Load8b::loadRHL),
            new InstDescription("ld (HL),r", "01_110_xxx", 0, Load8b::loadHLR),
            new InstDescription("ld (HL),n", "00_110_110", 1, Load8b::loadHLN),
            new InstDescription("ld A,(BC)", "00_001_010", 0, Load8b::loadA_BC),
            new InstDescription("ld A,(DE)", "00_011_010", 0, Load8b::loadA_DE),
            new InstDescription("ld A,(nn)", "11_111_010", 2, Load8b::loadA_nn),
            new InstDescription("ld (BC),A", "00_000_010", 0, Load8b::loadBC_A),
            new InstDescription("ld (DE),A", "00_010_010", 0, Load8b::loadDE_A),
            new InstDescription("ld (nn),A", "11_101_010", 2, Load8b::loadNN_A),
            new InstDescription("ld A,(FF00+n)", "11_110_000", 1, Load8b::loadA_n),
            new InstDescription("ld (FF00+n),A", "11_100_000", 1, Load8b::loadN_A),
            new InstDescription("ld A,(FF00+C)", "11_110_010", 0, Load8b::loadA_C),
            new InstDescription("ld (FF00+C),A", "11_100_010", 0, Load8b::loadC_A),
            new InstDescription("ldi (HL),A", "00_100_010", 0, Load8b::loadHLI_A),
            new InstDescription("ldi A,(HL)", "00_101_010", 0, Load8b::loadA_HLI),
            new InstDescription("ldd (HL),A", "00_110_010", 0, Load8b::loadHLD_A),
            new InstDescription("ldd A,(HL)", "00_111_010", 0, Load8b::loadA_HLD),

            //16-bit load commands
            new InstDescription("ld rr,nn", "00_xx0_001", 2, Load16b::loadRR_NN),
            new InstDescription("ld SP,HL", "11_111_001", 0, Load16b::loadSP_HL),
            new InstDescription("push rr", "11_xx0_101", 0, Load16b::push_QQ),
            new InstDescription("pop rr", "11_xx0_001", 0, Load16b::pop_QQ),

            //8-bit arithmetic
            new InstDescription("add A,r", "10_000_xxx", 0, Arithmetic8b::addA_r),
            new InstDescription("add A,n", "11_000_110", 1, Arithmetic8b::addA_n),
            new InstDescription("add A,(HL)", "10_000_110", 0, Arithmetic8b::addA_n),

            new InstDescription("adc A,r", "10_001_xxx", 0, Arithmetic8b::addA_r),
            new InstDescription("adc A,n", "11_001_110", 1, Arithmetic8b::addA_n),
            new InstDescription("adc A,(HL)", "10_001_110", 0, Arithmetic8b::addA_HL),

            new InstDescription("sub r", "10_010_xxx", 0, Arithmetic8b::sub_r),
            new InstDescription("sub n", "11_010_110", 1, Arithmetic8b::sub_n),
            new InstDescription("sub (HL)", "10_010_110", 0, Arithmetic8b::sub_HL),

            new InstDescription("sbc r", "10_011_xxx", 0, Arithmetic8b::sub_r),
            new InstDescription("sbc n", "11_011_110", 1, Arithmetic8b::sub_n),
            new InstDescription("sbc (HL)", "10_011_110", 0, Arithmetic8b::sub_HL),

            new InstDescription("and r", "10_100_xxx", 0, Arithmetic8b::and_r),
            new InstDescription("and n", "11_100_110", 1, Arithmetic8b::and_n),
            new InstDescription("and (HL)", "10_100_110", 0, Arithmetic8b::and_HL),

            new InstDescription("xor r", "10_101_xxx", 0, Arithmetic8b::xor_r),
            new InstDescription("xor n", "11_101_110", 1, Arithmetic8b::xor_n),
            new InstDescription("xor (HL)", "10_101_110", 0, Arithmetic8b::xor_HL),

            new InstDescription("or r", "10_110_xxx", 0, Arithmetic8b::or_r),
            new InstDescription("or n", "11_110_110", 1, Arithmetic8b::or_n),
            new InstDescription("or (HL)", "10_110_110", 0, Arithmetic8b::or_HL),

            new InstDescription("cp r", "10_111_xxx", 0, Arithmetic8b::cp_r),
            new InstDescription("cp n", "11_111_110", 1, Arithmetic8b::cp_n),
            new InstDescription("cp (HL)", "10_111_110", 0, Arithmetic8b::cp_HL),

            new InstDescription("inc r", "00_xxx_100", 0, Arithmetic8b::inc_r),
            new InstDescription("inc (HL)", "00_110_100", 0, Arithmetic8b::inc_HL),

            new InstDescription("dec r", "00_xxx_101", 0, Arithmetic8b::dec_r),
            new InstDescription("dec (HL)", "00_110_101", 0, Arithmetic8b::dec_HL),

            new InstDescription("daa", "00_100_111", 0, Arithmetic8b::daa),
            new InstDescription("cpl", "00_101_111", 0, Arithmetic8b::cpl),


            //16-bit arithmetic
            new InstDescription("add HL,rr", "00_xx1_001", 0, Arithmetic16b::addHL_rr),
            new InstDescription("inc rr", "00_xx0_011", 0, Arithmetic16b::inc_rr),
            new InstDescription("dec rr", "00_xx1_011", 0, Arithmetic16b::dec_rr),
            new InstDescription("add SP,dd", "11_101_000", 1, Arithmetic16b::addSP_dd),
            new InstDescription("ld HL,SP+dd", "11_111_000", 1, Arithmetic16b::loadHL_SPdd),

            //Rotate & shift
            new InstDescription("rlca", "00_000_111", 0, RotateShift::rlca),
            new InstDescription("rla", "00_010_111", 0, RotateShift::rla),
            new InstDescription("rrca", "00_001_111", 0, RotateShift::rrca),
            new InstDescription("rra", "00_011_111", 0, RotateShift::rra),
            new InstDescription("rlc r", 0xCB, "00_000_xxx", 0, RotateShift::rlc_r),
            new InstDescription("rlc (HL)", 0xCB, "00_000_110", 0, RotateShift::rlc_HL),
            new InstDescription("rl r", 0xCB, "00_010_xxx", 0, RotateShift::rl_r),
            new InstDescription("rl (HL)", 0xCB, "00_010_110", 0, RotateShift::rl_hl),
            new InstDescription("rrc r", 0xCB, "00_001_xxx", 0, RotateShift::rrc_r),
            new InstDescription("rrc (HL)", 0xCB, "00_001_110", 0, RotateShift::rrc_HL),
            new InstDescription("rr r", 0xCB, "00_011_xxx", 0, RotateShift::rr_r),
            new InstDescription("rr (HL)", 0xCB, "00_011_110", 0, RotateShift::rr_hl),
            new InstDescription("sla r", 0xCB, "00_100_xxx", 0, RotateShift::sla_r),
            new InstDescription("sla (HL)", 0xCB, "00_100_110", 0, RotateShift::sla_hl),
            new InstDescription("sra r", 0xCB, "00_101_xxx", 0, RotateShift::sra_r),
            new InstDescription("sra (HL)", 0xCB, "00_101_110", 0, RotateShift::sra_hl),
            new InstDescription("swap r", 0xCB, "00_110_xxx", 0, RotateShift::swap_r),
            new InstDescription("swap (HL)", 0xCB, "00_110_110", 0, RotateShift::swap_hl),
            new InstDescription("srl r", 0xCB, "00_111_xxx", 0, RotateShift::srl_r),
            new InstDescription("srl (HL)", 0xCB, "00_111_110", 0, RotateShift::srl_hl),

            //Single bit commands
            new InstDescription("bit n,r", 0xCB, "01_xxx_xxx", 0, SingleBit::bit_n_r),
            new InstDescription("bit n,(HL)", 0xCB, "01_xxx_110", 0, SingleBit::bit_n_HL),
            new InstDescription("set n,r", 0xCB, "11_xxx_xxx", 0, SingleBit::set_n_r),
            new InstDescription("set n,(HL)", 0xCB, "11_xxx_110", 0, SingleBit::set_n_HL),
            new InstDescription("res n,r", 0xCB, "10_xxx_xxx", 0, SingleBit::res_n_r),
            new InstDescription("res n,(HL)", 0xCB, "10_xxx_110", 0, SingleBit::res_n_HL),

            //Control commands
            new InstDescription("ccf", "00_111_111", 0, ControlCommands::ccf),
            new InstDescription("scf", "00_110_111", 0, ControlCommands::scf),
            new InstDescription("nop", "00_000_000", 0, ControlCommands::nop),
            new InstDescription("halt", "01_110_110", 0, ControlCommands::halt),
            new InstDescription("stop", "00_010_000", 1, ControlCommands::stop),    //It has 00_000_000 as second byte
            new InstDescription("di", "11_110_011", 0, ControlCommands::di),
            new InstDescription("ei", "11_111_011", 0, ControlCommands::ei),


            //Jump commands
            new InstDescription("jp nn", "11_000_011", 2, JumpCommands::jp_nn),
            new InstDescription("jp HL", "11_101_001", 0, JumpCommands::jp_HL),
            new InstDescription("jp f,nn", "11_0xx_010", 2, JumpCommands::jp_f_nn),
            new InstDescription("jr PC+dd", "00_011_000", 1, JumpCommands::jr_PC_dd),
            new InstDescription("jr f,PC+dd", "00_1xx_000", 1, JumpCommands::jr_f_PC_dd),
            new InstDescription("call nn", "11_001_101", 2, JumpCommands::call_nn),
            new InstDescription("call f,nn", "11_0xx_100", 2, JumpCommands::call_f_nn),
            new InstDescription("ret", "11_001_001", 0, JumpCommands::ret),
            new InstDescription("ret f", "11_0xx_000", 0, JumpCommands::ret_f),
            new InstDescription("reti", "11_011_001", 0, JumpCommands::reti),
            new InstDescription("rst n", "11_xxx_111", 0, JumpCommands::rst_n)
    };


}
