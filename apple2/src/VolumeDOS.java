import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/*
 * Created on Oct 13, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class VolumeDOS extends VolumeEntity
{
    private byte[] rb;
    private byte[] rbCmp;

    // parts of DOS to ignore for comparison purposes
    private static final int[] rIgnore1980 = {  0x00B4D, 0x00B58, 0x00B78, 0x00B79, 0x00B7B, 0x00B7B, 0x00B7D, 0x00BFF, 0x00D42,
            0x00D42, 0x0194F, 0x019B7, 0x02297, 0x024FF, 0x000B3, 0x000FF, 0x0018D, 0x00192, 0x001DF, 0x001FF, 0x003FD, 0x003FF,
            0x00469, 0x00495, 0x00500, 0x00655, 0x006DF, 0x006FF, 0x009A8, 0x009B7, 0x009D9, 0x009DB};

    private static final int[] rIgnore1983 = {  0x00B4D, 0x00B58, 0x00B7C, 0x00B7D, 0x00B7F, 0x00B7F, 0x00B81, 0x00BFF, 0x0C60, 0x0C61, 0x00D42,
            0x00D42, 0x0194F, 0x019B7, 0x0225D, 0x0225E, 0x02297, 0x024FF, 0x000B3, 0x000FF, 0x0018D, 0x00192, 0x001DF, 0x001FF,
            0x003FD, 0x003FF, 0x00494, 0x00495, 0x00500, 0x00655, 0x006DF, 0x006FF, 0x009A8, 0x009B7};

    private static final int[] rIgnore1986 = {  0x00B4D, 0x00B58, 0x00B7C, 0x00B7D, 0x00B7F, 0x00B7F, 0x00B81, 0x00BFF, 0x0C60, 0x0C61, 0x00D42,
            0x00D42, 0x0194F, 0x019B7, 0x0225D, 0x0225E, 0x02297, 0x024FF, 0x000CF, 0x000FF, 0x0018D, 0x00192, 0x001DF, 0x001FF,
            0x003FD, 0x003FF, 0x00484, 0x00495, 0x00500, 0x00655, 0x006DF, 0x006FF, 0x009A8, 0x009B7};

//    0x0148, // master: 03 slave: 84
//    0x0149, // master: 1B slave: 9D
//    0x0C0D, // master: 1B slave: 9D
//    0x0C56, // master: 36 slave: FC
//    0x0C57, // master: E8 slave: A4
//    0x0C58, // master: E5 slave: FC
//    0x0C59,
//    0x0C5A, // master: E3 slave: 65
//    0x0C5B, // master: E3 slave: D8
//    0x0C5E, // master: 03 slave: 3C
//    0x0C5F, // master: E0 slave: D4
//    0x0A00-0x0BFF // T$00,S$A-$B DOS reloc routine

    private static final int[] rSlave1980 = {0x0060, 0x0068, 0x006D, 0x0070, 0x0073, 0x0078, 0x007D, 0x0082, 0x0085, 0x0088,
            0x008B, 0x008E, 0x0091, 0x00A3, 0x00AB, 0x00B0, 0x0102, 0x0105, 0x010A, 0x010D, 0x0110, 0x0113, 0x0118, 0x011D, 0x0120,
            0x0124, 0x0129, 0x013A, 0x0140, 0x0143, 0x014C, 0x0150, 0x0153, 0x0156, 0x0159, 0x015C, 0x0161, 0x0166, 0x016B, 0x016E,
            0x0171, 0x0174, 0x017A, 0x017F, 0x0185, 0x0188, 0x018B, 0x0195, 0x0198, 0x019B, 0x019E, 0x01A8, 0x01AB, 0x01AE, 0x01B1,
            0x01B9, 0x01C4, 0x01C7, 0x01CC, 0x01CF, 0x01D4, 0x020A, 0x020E, 0x0211, 0x0220, 0x0225, 0x023A, 0x024E, 0x0256, 0x025B,
            0x0260, 0x0268, 0x026B, 0x026F, 0x0280, 0x0284, 0x0290, 0x0297, 0x029C, 0x02A1, 0x02A6, 0x02AB, 0x02B0, 0x02CB, 0x02CE,
            0x02D2, 0x030B, 0x0310, 0x031C, 0x0321, 0x032C, 0x03D3, 0x03D6, 0x03D9, 0x03DF, 0x03E2, 0x03E5, 0x03EC, 0x066B, 0x066E,
            0x067D, 0x0682, 0x0687, 0x068C, 0x0691, 0x0696, 0x06AC, 0x06B1, 0x06B6, 0x06BB, 0x0789, 0x0796, 0x07BB, 0x07C6, 0x07D6,
            0x07E5, 0x07E9, 0x07EC, 0x07FB, 0x080A, 0x080F, 0x082D, 0x0837, 0x0842, 0x0853, 0x0866, 0x086F, 0x088D, 0x089F, 0x08C1,
            0x08C7, 0x08CF, 0x08D8, 0x08DB, 0x08ED, 0x08F6, 0x0919, 0x091E, 0x0934, 0x093C, 0x093F, 0x0942, 0x094B, 0x0964, 0x0969,
            0x0973, 0x097E, 0x098C, 0x0993, 0x09D8, 0x09DE, 0x09E1, 0x09E4, 0x09E8, 0x09EB, 0x09EF, 0x09F2, 0x09F6, 0x09FA, 0x09FF,
            0x0C01, 0x0C03, 0x0C05, 0x0C07, 0x0C09, 0x0C0B, 0x0C0F, 0x0C11, 0x0C13, 0x0C15, 0x0C17, 0x0C19, 0x0C1B, 0x0C1D, 0x0C1F,
            0x0C21, 0x0C23, 0x0C25, 0x0C27, 0x0C29, 0x0C2B, 0x0C2D, 0x0C2F, 0x0C31, 0x0C33, 0x0C35, 0x0C37, 0x0C39, 0x0C3B, 0x0C3D,
            0x0C3F, 0x0C41, 0x0C43, 0x0C45, 0x0C47, 0x0C49, 0x0C4B, 0x0C4D, 0x0C4F, 0x0C51, 0x0C53, 0x0C55, 0x0C65, 0x0C6D, 0x0C6F,
            0x0C79, 0x0C7B, 0x0C7F, 0x0C86, 0x0C8D, 0x0C90, 0x0C93, 0x0C9D, 0x0CA2, 0x0CA5, 0x0CAB, 0x0CB0, 0x0CB5, 0x0CB8, 0x0CC1,
            0x0CCF, 0x0CD4, 0x0CD9, 0x0CDC, 0x0CE1, 0x0CE6, 0x0CE9, 0x0CEF, 0x0CF4, 0x0CF7, 0x0CFF, 0x0D02, 0x0D08, 0x0D0B, 0x0D0E,
            0x0D11, 0x0D17, 0x0D1F, 0x0D22, 0x0D29, 0x0D32, 0x0D3D, 0x0D47, 0x0D4C, 0x0D4F, 0x0D53, 0x0D56, 0x0D59, 0x0D5C, 0x0D5F,
            0x0D62, 0x0D66, 0x0D69, 0x0D6D, 0x0D83, 0x0D86, 0x0D8C, 0x0D94, 0x0D97, 0x0DA0, 0x0DA5, 0x0DAA, 0x0DAD, 0x0DB0, 0x0DB3,
            0x0DB6, 0x0DB9, 0x0DBF, 0x0DC2, 0x0DC7, 0x0DCB, 0x0DCF, 0x0DD3, 0x0DD6, 0x0DD9, 0x0DDF, 0x0DE4, 0x0DED, 0x0DF2, 0x0DF5,
            0x0E04, 0x0E07, 0x0E0D, 0x0E11, 0x0E14, 0x0E1B, 0x0E22, 0x0E2B, 0x0E2E, 0x0E33, 0x0E3A, 0x0E43, 0x0E48, 0x0E4E, 0x0E51,
            0x0E5A, 0x0E5D, 0x0E60, 0x0E63, 0x0E6E, 0x0E75, 0x0E7C, 0x0E7F, 0x0E82, 0x0E88, 0x0E94, 0x0EA1, 0x0EA6, 0x0EA9, 0x0EAC,
            0x0EAF, 0x0EB2, 0x0EB5, 0x0EB8, 0x0EBC, 0x0EBF, 0x0EC2, 0x0ECC, 0x0ED1, 0x0ED5, 0x0ED8, 0x0EE1, 0x0EE7, 0x0EEA, 0x0EEF,
            0x0EFD, 0x0F05, 0x0F0A, 0x0F14, 0x0F17, 0x0F1A, 0x0F1D, 0x0F20, 0x0F23, 0x0F2A, 0x0F31, 0x0F3C, 0x0F41, 0x0F45, 0x0F4F,
            0x0F53, 0x0F56, 0x0F5B, 0x0F63, 0x0F6B, 0x0F70, 0x0F7A, 0x0F81, 0x0F88, 0x0F8D, 0x0F94, 0x0F9B, 0x0FA2, 0x0FA7, 0x0FAC,
            0x0FB8, 0x0FBD, 0x0FCD, 0x0FD0, 0x0FD5, 0x0FD8, 0x0FDB, 0x0FDE, 0x0FE1, 0x0FE4, 0x0FE7, 0x0FEA, 0x0FF3, 0x0FF6, 0x0FF9,
            0x0FFC, 0x1001, 0x1006, 0x1009, 0x1010, 0x101A, 0x101F, 0x1022, 0x1026, 0x1029, 0x102E, 0x103A, 0x1041, 0x104A, 0x1051,
            0x105B, 0x1060, 0x1063, 0x1069, 0x106C, 0x1072, 0x1075, 0x107C, 0x107F, 0x1082, 0x1085, 0x1088, 0x108C, 0x1090, 0x1095,
            0x10A0, 0x10A6, 0x10B4, 0x10C1, 0x10CA, 0x10CD, 0x10E1, 0x10EC, 0x10EF, 0x1106, 0x111E, 0x1128, 0x1135, 0x1138, 0x113B,
            0x113F, 0x1144, 0x1149, 0x114C, 0x114F, 0x1155, 0x115B, 0x115F, 0x1162, 0x1167, 0x116A, 0x1179, 0x117C, 0x1183, 0x1186,
            0x1189, 0x118C, 0x1191, 0x1194, 0x1197, 0x119A, 0x119D, 0x11A2, 0x11A7, 0x11AC, 0x11AF, 0x11B4, 0x11BB, 0x11BE, 0x11C1,
            0x11C4, 0x11C7, 0x11CA, 0x11D1, 0x11DA, 0x11DD, 0x11E0, 0x11E3, 0x11E6, 0x11E9, 0x11EC, 0x11F3, 0x11F8, 0x11FB, 0x11FE,
            0x1205, 0x120D, 0x1212, 0x1215, 0x1218, 0x121D, 0x1222, 0x1227, 0x122C, 0x122F, 0x1235, 0x123C, 0x1241, 0x1244, 0x1247,
            0x124A, 0x124D, 0x1250, 0x1253, 0x1256, 0x1259, 0x125C, 0x125F, 0x1264, 0x126B, 0x1270, 0x1273, 0x1277, 0x127E, 0x1281,
            0x1284, 0x1287, 0x128A, 0x128D, 0x1290, 0x1293, 0x1296, 0x1299, 0x12A2, 0x12A7, 0x12B4, 0x12BB, 0x12C0, 0x12CD, 0x12D4,
            0x12D7, 0x12DB, 0x12DF, 0x12E2, 0x12E5, 0x12E8, 0x12ED, 0x12F2, 0x12F5, 0x12F8, 0x12FB, 0x12FE, 0x1301, 0x1304, 0x1309,
            0x130C, 0x130F, 0x1312, 0x1315, 0x1318, 0x131D, 0x1322, 0x1325, 0x132C, 0x132F, 0x1349, 0x134C, 0x134F, 0x1354, 0x1357,
            0x135D, 0x1363, 0x1373, 0x1376, 0x1379, 0x137C, 0x137F, 0x1382, 0x1385, 0x138A, 0x138F, 0x1394, 0x1399, 0x139C, 0x139F,
            0x13A2, 0x13A6, 0x13A9, 0x13AD, 0x13B0, 0x13B3, 0x13B8, 0x13BB, 0x13C1, 0x13C6, 0x13C9, 0x13CF, 0x13D3, 0x13D8, 0x13DB,
            0x13DE, 0x13E1, 0x13E4, 0x13EF, 0x13F2, 0x13F5, 0x13F8, 0x13FB, 0x1412, 0x1417, 0x141A, 0x141D, 0x1422, 0x1425, 0x1428,
            0x142D, 0x1430, 0x1433, 0x1436, 0x143F, 0x1442, 0x144A, 0x144D, 0x1453, 0x1458, 0x145F, 0x1462, 0x1465, 0x146A, 0x146D,
            0x1472, 0x1475, 0x1478, 0x147E, 0x1485, 0x148A, 0x148F, 0x1492, 0x149A, 0x149D, 0x14A2, 0x14A9, 0x14AE, 0x14B1, 0x14C8,
            0x14CB, 0x14CE, 0x14D1, 0x14D4, 0x14D7, 0x14DA, 0x14DF, 0x14E4, 0x14E7, 0x14EA, 0x14ED, 0x14F4, 0x14F9, 0x14FE, 0x1501,
            0x1504, 0x1510, 0x1515, 0x1518, 0x151D, 0x1522, 0x1525, 0x1528, 0x152F, 0x1532, 0x1537, 0x153C, 0x1543, 0x154C, 0x154F,
            0x155D, 0x1561, 0x157B, 0x157E, 0x1581, 0x1584, 0x1587, 0x1590, 0x1595, 0x1598, 0x159B, 0x159F, 0x15A4, 0x15AA, 0x15AF,
            0x15B6, 0x15B9, 0x15C2, 0x15D4, 0x15D7, 0x15DA, 0x15E5, 0x15E8, 0x15EB, 0x15EE, 0x15F1, 0x15F4, 0x15F7, 0x15FE, 0x1601,
            0x1604, 0x1608, 0x160B, 0x1611, 0x1614, 0x161C, 0x161F, 0x1622, 0x1625, 0x1628, 0x162B, 0x162E, 0x1631, 0x1634, 0x1637,
            0x163C, 0x1641, 0x1647, 0x1654, 0x165F, 0x1662, 0x166A, 0x166D, 0x1670, 0x1675, 0x1688, 0x1694, 0x1697, 0x16B1, 0x16B6,
            0x16BD, 0x16C6, 0x16CF, 0x16D2, 0x16D7, 0x16DC, 0x16E1, 0x16E4, 0x1713, 0x172C, 0x1738, 0x1755, 0x175A, 0x175F, 0x1762,
            0x1767, 0x176E, 0x1773, 0x1778, 0x177B, 0x1780, 0x19C2, 0x19C4, 0x19C6, 0x19C8, 0x19CA, 0x19CC, 0x19CE, 0x19D0, 0x19D2,
            0x19D4, 0x19D6, 0x19D8, 0x19DA, 0x19DC, 0x19DE, 0x19E0, 0x19E2, 0x19E4, 0x19E6, 0x19E8, 0x19EA, 0x19EC, 0x19EE, 0x19F0,
            0x19F2, 0x19F4, 0x19F6, 0x19F8, 0x19FA, 0x19FC, 0x1A05, 0x1A09, 0x1A0C, 0x1A0F, 0x1A18, 0x1A1C, 0x1A21, 0x1A24, 0x1A27,
            0x1A2A, 0x1A2F, 0x1A32, 0x1A35, 0x1A3F, 0x1A42, 0x1A45, 0x1A4A, 0x1A4D, 0x1A50, 0x1A53, 0x1A59, 0x1A60, 0x1A63, 0x1A68,
            0x1A6D, 0x1A70, 0x1A73, 0x1A76, 0x1A79, 0x1A7C, 0x1A7F, 0x1A82, 0x1A85, 0x1A88, 0x1A8B, 0x1A8E, 0x1A91, 0x1A94, 0x1A97,
            0x1A9A, 0x1A9D, 0x1AA0, 0x1AA5, 0x1AA8, 0x1AAB, 0x1AAE, 0x1AB1, 0x1AB4, 0x1AB7, 0x1ABA, 0x1ABD, 0x1AC0, 0x1AC3, 0x1AC6,
            0x1AC9, 0x1ACE, 0x1AD1, 0x1AD4, 0x1AD7, 0x1ADB, 0x1AE1, 0x1AE9, 0x1AEE, 0x1AF1, 0x1AF4, 0x1AF7, 0x1AFF, 0x1B04, 0x1B08,
            0x1B0B, 0x1B0E, 0x1B13, 0x1B18, 0x1B1E, 0x1B22, 0x1B27, 0x1B2A, 0x1B2D, 0x1B30, 0x1B33, 0x1B36, 0x1B39, 0x1B3C, 0x1B3F,
            0x1B44, 0x1B49, 0x1B4E, 0x1B51, 0x1B54, 0x1B57, 0x1B5A, 0x1B63, 0x1B67, 0x1B6C, 0x1B6F, 0x1B72, 0x1B77, 0x1B80, 0x1B84,
            0x1B89, 0x1B8C, 0x1B8F, 0x1B92, 0x1B95, 0x1B98, 0x1B9B, 0x1B9F, 0x1BA7, 0x1BAA, 0x1BB2, 0x1BB5, 0x1BBA, 0x1BBD, 0x1BC0,
            0x1BC3, 0x1BC6, 0x1BC9, 0x1BCC, 0x1BD3, 0x1BD6, 0x1BD9, 0x1BDD, 0x1BE5, 0x1BE8, 0x1BEB, 0x1BEE, 0x1BF3, 0x1BFA, 0x1BFD,
            0x1C00, 0x1C03, 0x1C08, 0x1C0B, 0x1C0E, 0x1C11, 0x1C14, 0x1C17, 0x1C1A, 0x1C1D, 0x1C22, 0x1C27, 0x1C2A, 0x1C2D, 0x1C30,
            0x1C33, 0x1C38, 0x1C3B, 0x1C3E, 0x1C41, 0x1C44, 0x1C49, 0x1C4C, 0x1C4F, 0x1C52, 0x1C56, 0x1C5B, 0x1C60, 0x1C6F, 0x1C72,
            0x1C79, 0x1C7C, 0x1C7F, 0x1C85, 0x1C88, 0x1C8C, 0x1C93, 0x1C9A, 0x1C9F, 0x1CA2, 0x1CA7, 0x1CAA, 0x1CAD, 0x1CB2, 0x1CBD,
            0x1CC2, 0x1CC5, 0x1CC8, 0x1CCC, 0x1CD3, 0x1CD6, 0x1CDF, 0x1CEA, 0x1CF8, 0x1D03, 0x1D08, 0x1D0D, 0x1D1A, 0x1D24, 0x1D27,
            0x1D2E, 0x1D36, 0x1D40, 0x1D4B, 0x1D50, 0x1D5F, 0x1D6C, 0x1D71, 0x1D76, 0x1D80, 0x1D85, 0x1D90, 0x1D95, 0x1D98, 0x1D9D,
            0x1DA2, 0x1DA7, 0x1DAE, 0x1DBC, 0x1DBF, 0x1DCE, 0x1DD4, 0x1DDA, 0x1DDF, 0x1DE4, 0x1DE7, 0x1DEA, 0x1DEE, 0x1DF3, 0x1DF6,
            0x1E01, 0x1E04, 0x1E07, 0x1E14, 0x1E19, 0x1E1F, 0x1E25, 0x1E2A, 0x1E2F, 0x1E32, 0x1E36, 0x1E3C, 0x1E41, 0x1E46, 0x1E49,
            0x1E4D, 0x1E50, 0x1E53, 0x1E56, 0x1E59, 0x1E5C, 0x1E61, 0x1E64, 0x1E67, 0x1E6D, 0x1E70, 0x1E73, 0x1E81, 0x1E84, 0x1E8D,
            0x1E96, 0x1E9C, 0x1E9F, 0x1EA4, 0x1EAA, 0x1EB9, 0x1EBC, 0x1EBF, 0x1EC6, 0x1ECA, 0x1ECD, 0x1ED3, 0x1ED6, 0x1ED9, 0x1EDE,
            0x1EE3, 0x1EE6, 0x1EE9, 0x1EEC, 0x1EEF, 0x1EF2, 0x1EF5, 0x1EFF, 0x1F02, 0x1F05, 0x1F08, 0x1F0B, 0x1F10, 0x1F14, 0x1F1A,
            0x1F1D, 0x1F22, 0x1F29, 0x1F2C, 0x1F2F, 0x1F34, 0x1F39, 0x1F3C, 0x1F3F, 0x1F44, 0x1F47, 0x1F4A, 0x1F4D, 0x1F50, 0x1F54,
            0x1F57, 0x1F5A, 0x1F61, 0x1F64, 0x1F67, 0x1F6C, 0x1F6F, 0x1F72, 0x1F75, 0x1F78, 0x1F7B, 0x1F7E, 0x1F81, 0x1F84, 0x1F89,
            0x1F8C, 0x1F8F, 0x1F92, 0x1F95, 0x1F98, 0x1F9D, 0x1FA3, 0x1FB5, 0x1FB8, 0x1FBB, 0x1FC0, 0x1FC3, 0x1FC8, 0x1FCB, 0x1FCE,
            0x1FD5, 0x1FD8, 0x1FDD, 0x1FE0, 0x1FE7, 0x1FEA, 0x1FEF, 0x1FF6, 0x1FF9, 0x2000, 0x2007, 0x2010, 0x2013, 0x2016, 0x201C,
            0x201F, 0x2022, 0x2025, 0x2028, 0x202B, 0x202E, 0x2031, 0x2036, 0x2039, 0x203C, 0x2042, 0x2046, 0x204B, 0x204E, 0x2051,
            0x2056, 0x2059, 0x205D, 0x2060, 0x2063, 0x2066, 0x2069, 0x206C, 0x206F, 0x2072, 0x2079, 0x207E, 0x2087, 0x208C, 0x208F,
            0x2092, 0x2096, 0x209B, 0x20A0, 0x20A4, 0x20A7, 0x20AE, 0x20B3, 0x20B7, 0x20BC, 0x20C1, 0x20C4, 0x20C8, 0x20CB, 0x20CE,
            0x20D3, 0x20DA, 0x20DF, 0x20E3, 0x20E6, 0x20ED, 0x20F0, 0x20FE, 0x2108, 0x210D, 0x2114, 0x2119, 0x2125, 0x212D, 0x2133,
            0x213E, 0x2143, 0x2146, 0x214B, 0x2153, 0x215B, 0x2160, 0x2163, 0x2169, 0x216E, 0x2171, 0x2175, 0x2178, 0x217D, 0x2186,
            0x218D, 0x2190, 0x2196, 0x2199, 0x21A3, 0x21A6, 0x21AE, 0x21B7, 0x21BA, 0x21BD, 0x21C2, 0x21C5, 0x21CC, 0x21CF, 0x21D4,
            0x21D9, 0x21DC, 0x21E1, 0x21E8, 0x21F4, 0x21F7, 0x21FA, 0x2202, 0x2205, 0x2208, 0x220B, 0x220E, 0x2211, 0x2216, 0x221C,
            0x2226, 0x2229, 0x222C, 0x2230, 0x2234, 0x2237, 0x223A, 0x2240, 0x2243, 0x2246, 0x2249, 0x224C, 0x224F, 0x2252, 0x2255,
            0x225A, 0x225D, 0x2279, 0x2281, 0x2289, 0x2290, 0x2294};

    private static List rPosDOS = new ArrayList();
    private static byte[] rbClear1980 = new byte[0x4000-0x1B00];
    private static byte[] rbClear1983 = new byte[0x4000-0x1B00];
    private static byte[] rbClear1986 = new byte[0x4000-0x1B00];
    static
    {
        DiskPos p, pLim;
        try
        {
            p = new DiskPos(0,0);
            pLim = new DiskPos(2,5);
        }
        catch (InvalidPosException e)
        {
            throw new RuntimeException(e);
        }
        while (!p.equals(pLim))
        {
            rPosDOS.add(p.clone());
            p.advance(DiskPos.cSector);
        }

        try
        {
            BufferedInputStream in = new BufferedInputStream(VolumeDOS.class.getClassLoader().getResourceAsStream("dos33_1980_clear.bin"));
            int x = 0;
            int b = in.read();
            while (b != -1)
            {
                rbClear1980[x++] = (byte)b;
                b = in.read();
            }
            in.close();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        try
        {
            BufferedInputStream in = new BufferedInputStream(VolumeDOS.class.getClassLoader().getResourceAsStream("dos33_1983_clear.bin"));
            int x = 0;
            int b = in.read();
            while (b != -1)
            {
                rbClear1983[x++] = (byte)b;
                b = in.read();
            }
            in.close();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        try
        {
            BufferedInputStream in = new BufferedInputStream(VolumeDOS.class.getClassLoader().getResourceAsStream("dos33_1986_clear.bin"));
            int x = 0;
            int b = in.read();
            while (b != -1)
            {
                rbClear1986[x++] = (byte)b;
                b = in.read();
            }
            in.close();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @param disk
     */
    public void readFromMedia(Disk disk)
    {
        int x = 0;
        for (Iterator i = rPosDOS.iterator(); i.hasNext();)
        {
            DiskPos p = (DiskPos)i.next();
            rSector.add(new VolumeSector(p,x++,this));
        }

        List rPos = new ArrayList();
        getUsed(rPos);
        rb = disk.getDos33File(rPos);
        rbCmp = new byte[rb.length];
        System.arraycopy(rb, 0, rbCmp, 0, rb.length);
    }

    /**
     * @param rPos
     */
    public void getUsed(Collection rPos)
    {
        getPos(rPos);
    }

    /**
     * @param s
     */
    public void dump(StringBuffer s)
    {
        s.append("DOS");
        appendSig(s);
        s.append(": ");
        for (Iterator i = this.rSector.iterator(); i.hasNext();)
        {
            VolumeSector sect = (VolumeSector)i.next();
            s.append(sect.toString());
            if (i.hasNext())
            {
                s.append("; ");
            }
        }
        s.append("\n");
    }

    /**
     * @return
     */
    public boolean hasProntodosSignature()
    {
        return (rb[0x1602] == 0x54 && rb[0x1603] == 0x59 && rb[0x1604] == 0x50 && rb[0x1605] == 0xFFFFFFC5);
    }
    /**
     * @return
     */
    public boolean hasDaviddosSignature()
    {
        return (rb[0x1924] == 0x44 && rb[0x1925] == 0x2D && rb[0x1926] == 0x44 && rb[0x1927] == 0x4F && rb[0x1928] == 0x53);
    }
    /**
     * @return
     */
    public boolean hasDaviddos2Signature()
    {
//        return hasDaviddosSignature() && 
//        (rb[0x2701] == 0xFFFFFFC4 && rb[0x2702] == 0xFFFFFFC1 && rb[0x2703] == 0xFFFFFFD6 && rb[0x270B] == 0xFFFFFFC9 && rb[0x270C] == 0xFFFFFFC9);
        // TODO fix daviddos2
        return false;
    }
    /**
     * @return
     */
    public boolean hasDiversidos2cSignature()
    {
        return (rb[0xA10] == 0x20 && rb[0xA11] == (byte)0x84 && rb[0xA12] == 0xFFFFFF9D && rb[0xA13] == 0xFFFFFFA0);
    }
    /**
     * @return
     */
    public boolean hasDiversidos41cSignature()
    {
        return (rb[0xA10] == 0xFFFFFFA9 && rb[0xA11] == 0xFFFFFFFF && rb[0xA12] == 0xFFFFFF8D && rb[0xA13] == 0xFFFFFFFB);
    }
    /**
     * @return
     */
    public boolean hasEsdosSignature()
    {
        return (rb[0x1671] == 0x4C && rb[0x1672] == 0x4E && rb[0x1673] == 0xFFFFFFC1 && rb[0x1674] == 0x52);
    }
    /**
     * @return
     */
    public boolean hasHyperdosSignature()
    {
        return (rb[0x656] == 0xFFFFFFAD && rb[0x657] ==  0x61 && rb[0x658] ==  0xFFFFFFAA && rb[0x659] ==  0xFFFFFFC9 && rb[0x65A] ==  0x01 && rb[0x65B] ==  0xFFFFFFB0);
    }
    /**
     * @return
     */
    public boolean hasRdosSignature()
    {
        return (rb[0x100] == 0x4C && rb[0x101] ==  0x74 && rb[0x102] ==  0xFFFFFFB9 && rb[0x103] ==  0xFFFFFFA0);
    }

    /**
     * @param s
     */
    public void appendSig(StringBuffer s)
    {
        boolean alt = false;
        if (hasDaviddosSignature())
        {
            s.append(" (David DOS)");
            alt = true;
        }
        if (hasDaviddos2Signature())
        {
            s.append(" (David DOS II)");
            alt = true;
        }
        if (hasDiversidos2cSignature())
        {
            s.append(" (Diversi-DOS 2-C)");
            alt = true;
        }
        if (hasDiversidos41cSignature())
        {
            s.append(" (Diversi-DOS 4.1-C)");
            alt = true;
        }
        if (hasEsdosSignature())
        {
            s.append(" (ES DOS)");
            alt = true;
        }
        if (hasHyperdosSignature())
        {
            s.append(" (Hyper-DOS)");
            alt = true;
        }
        if (this.hasProntodosSignature())
        {
            s.append(" (Pronto-DOS [Beagle Bros.])");
            alt = true;
        }
        if (this.hasRdosSignature())
        {
            s.append(" (RDOS [SSI])");
            alt = true;
        }
        // TODO AMDOS signature
        // TODO Franklin signature

        // various DOS 3.3 signatures
        if (!alt)
        {
            /*
             * Assume is normal DOS 3.3, so find out which
             * version (1980, 1983, or 1986).
             */
            int tempdostype = 0;
            int x = rb[0x84] & 0xFF;
            if (x == 0x46)
            {
                tempdostype = 1980;
                clearIgnored(rIgnore1980);
                if (Arrays.equals(rbCmp,rbClear1980))
                {
                    s.append(" (DOS 3.3 1980 exact match)");
                }
                else
                {
                    // TODO check for slave DOS
                    int dif = rb[0xFE]-0x36;
                    byte[] rbClearSlave = makeSlave(rbClear1980,rSlave1980,dif);
                    
                    s.append(" (DOS 3.3 1980 altered)");
                }
            }
            else if (x == 0x84)
            {
                tempdostype = 1983;
                clearIgnored(rIgnore1983);
                if (Arrays.equals(rbCmp,rbClear1983))
                {
                    s.append(" (DOS 3.3 1983 exact match)");
                }
                else
                {
                    s.append(" (DOS 3.3 1983 altered)");
                }
            }
            else if (x == 0xB3)
            {
                tempdostype = 1986;
                clearIgnored(rIgnore1986);
                if (Arrays.equals(rbCmp,rbClear1986))
                {
                    s.append(" (DOS 3.3 1986 exact match)");
                }
                else
                {
                    s.append(" (DOS 3.3 1986 altered)");
                }
            }
        }
    }

    /**
     * @param rbClear
     * @param rbSlaveOffset
     * @param dif
     * @return
     */
    private byte[] makeSlave(byte[] rbClear, int[] rbSlaveOffset, int dif)
    {
        // copy entire master image
        byte[] rbSlave = new byte[rbClear.length];
        System.arraycopy(rbClear, 0, rbSlave, 0, rbClear.length);

        // apply diff to master image (at all known offsets)
        for (int i = 0; i < rbSlaveOffset.length; i++)
        {
            int bOff = rbSlaveOffset[i];
            rbSlave[bOff] -= dif;
        }

        // clear reloc routine:
        for (int i = 0x0A00; i < 0x0C00; ++i)
        {
            rbSlave[i] = 0;
        }

        // change the remaining differences
//      0x0148, // master: 03 slave: 84
        rbSlave[0x0148] = (byte)0x84;
//      0x0149, // master: 1B slave: 9D
        rbSlave[0x0149] = (byte)0x9D;
//      0x0C0D, // master: 1B slave: 9D
        rbSlave[0x0C0D] = (byte)0x9D;
//      0x0C56, // master: 36 slave: FC
        rbSlave[0x0C56] = (byte)0xFC;
//      0x0C57, // master: E8 slave: A4
        rbSlave[0x0C57] = (byte)0xA4;
//      0x0C58, // master: E5 slave: FC
        rbSlave[0x0C58] = (byte)0xFC;
//      0x0C59 (same)
//      0x0C5A, // master: E3 slave: 65
        rbSlave[0x0C5A] = (byte)0x65;
//      0x0C5B, // master: E3 slave: D8
        rbSlave[0x0C5B] = (byte)0xD8;
//      0x0C5E, // master: 03 slave: 3C
        rbSlave[0x0C5E] = (byte)0x3C;
//      0x0C5F, // master: E0 slave: D4
        rbSlave[0x0C5F] = (byte)0xD4;

        return rbSlave;
    }

    /**
     * @param rIgn
     */
    private void clearIgnored(int[] rIgn)
    {
        for (int i = 0; i < rIgn.length/2; ++i)
        {
            for (int b = rIgn[i*2]; b <= rIgn[i*2+1]; ++b)
            {
                rbCmp[b] = 0;
            }
        }
    }

    /**
     * @param knownSectors
     * @return
     */
    public static boolean isDOSKnown(Collection knownSectors)
    {
        int c = 0x1f; // check T$00,S$1 thru T$01,S$F only (e.g. Prontodos)
        for (Iterator i = rPosDOS.iterator(); i.hasNext() && c-- > 0;)
        {
            DiskPos p = (DiskPos)i.next();
            if (knownSectors.contains(p))
            {
                return true;
            }
        }
        return false;
    }
}
