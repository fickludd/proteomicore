package se.lth.immun.chem;

public enum Element {
	H 	("H", 	1, 	1.007940f, 	new float[]{1.00782503207f, 2.0141017778f, 3.0160492777f}, new float[]{0.999885f, 0.000115f, 0.0f}, new int[]{1, 2, 3}),
	H2 	("2H", 	1, 	2.0141017778f, 	new float[]{2.0141017778f}, new float[]{1.0f}, new int[]{2}),
	He 	("He", 	2, 	4.002602f, 	new float[]{3.0160293191f, 4.00260325415f}, new float[]{1.34e-06f, 0.99999866f}, new int[]{3, 4}),
	Li 	("Li", 	3, 	6.941000f, 	new float[]{6.015122795f, 7.01600455f}, new float[]{0.0759f, 0.9241f}, new int[]{6, 7}),
	Be 	("Be", 	4, 	9.012182f, 	new float[]{9.0121822f}, new float[]{1.0f}, new int[]{9}),
	B 	("B", 	5, 	10.811000f, 	new float[]{10.012937f, 11.0093054f}, new float[]{0.199f, 0.801f}, new int[]{10, 11}),
	C 	("C", 	6, 	12.010700f, 	new float[]{12.0f, 13.0033548378f, 14.003241989f}, new float[]{0.9893f, 0.0107f, 0.0f}, new int[]{12, 13, 14}),
	C13 ("13C", 6, 	13.0033548378f, new float[]{13.0033548378f}, new float[]{1.0f}, new int[]{13}),
	N 	("N", 	7, 	14.006700f, 	new float[]{14.0030740048f, 15.0001088982f}, new float[]{0.99636f, 0.00364f}, new int[]{14, 15}),
	N15 ("15N", 7, 	15.0001088982f, new float[]{15.0001088982f}, new float[]{1.0f}, new int[]{15}),
	O 	("O", 	8, 	15.999400f, 	new float[]{15.99491461956f, 16.9991317f, 17.999161f}, new float[]{0.99757f, 0.00038f, 0.00205f}, new int[]{16, 17, 18}),
	O18 ("18O", 8, 	17.999161f, 	new float[]{17.999161f}, new float[]{1.0f}, new int[]{18}),
	F 	("F", 	9, 	18.998403f, 	new float[]{18.99840322f}, new float[]{1.0f}, new int[]{19}),
	Ne 	("Ne", 	10, 20.179700f, 	new float[]{19.9924401754f, 20.99384668f, 21.991385114f}, new float[]{0.9048f, 0.0027f, 0.0925f}, new int[]{20, 21, 22}),
	Na 	("Na", 	11, 22.989769f, 	new float[]{22.9897692809f}, new float[]{1.0f}, new int[]{23}),
	Mg 	("Mg", 	12, 24.305000f, 	new float[]{23.9850417f, 24.98583692f, 25.982592929f}, new float[]{0.7899f, 0.1f, 0.1101f}, new int[]{24, 25, 26}),
	Al 	("Al", 	13, 26.981539f, 	new float[]{26.98153863f}, new float[]{1.0f}, new int[]{27}),
	Si 	("Si", 	14, 28.085500f, 	new float[]{27.9769265325f, 28.9764947f, 29.97377017f}, new float[]{0.92223f, 0.04685f, 0.03092f}, new int[]{28, 29, 30}),
	P 	("P", 	15, 30.973762f, 	new float[]{30.97376163f}, new float[]{1.0f}, new int[]{31}),
	S 	("S", 	16, 32.065000f, 	new float[]{31.972071f, 32.97145876f, 33.9678669f, 35.96708076f}, new float[]{0.9499f, 0.0075f, 0.0425f, 0.0001f}, new int[]{32, 33, 34, 36}),
	Cl 	("Cl", 	17, 35.453000f, 	new float[]{34.96885268f, 36.96590259f}, new float[]{0.7576f, 0.2424f}, new int[]{35, 37}),
	Ar 	("Ar", 	18, 39.948000f, 	new float[]{35.96754510f, 37.9627324f, 39.9623831225f}, new float[]{0.003365f, 0.000632f, 0.996003f}, new int[]{36, 38, 40}),
	K 	("K", 	19, 39.098300f, 	new float[]{38.96370668f, 39.96399848f, 40.96182576f}, new float[]{0.932581f, 0.000117f, 0.067302f}, new int[]{39, 40, 41}),
	Ca 	("Ca", 	20, 40.078000f, 	new float[]{39.96259098f, 41.95861801f, 42.9587666f, 43.9554818f, 45.9536926f, 47.952534f}, new float[]{0.96941f, 0.00647f, 0.00135f, 0.02086f, 4e-05f, 0.00187f}, new int[]{40, 42, 43, 44, 46, 48}),
	Sc 	("Sc", 	21, 44.955912f, 	new float[]{44.9559119f}, new float[]{1.0f}, new int[]{45}),
	Ti 	("Ti", 	22, 47.867000f, 	new float[]{45.9526316f, 46.9517631f, 47.9479463f, 48.94787f, 49.9447912f}, new float[]{0.0825f, 0.0744f, 0.7372f, 0.0541f, 0.0518f}, new int[]{46, 47, 48, 49, 50}),
	V 	("V", 	23, 50.941500f, 	new float[]{49.9471585f, 50.9439595f}, new float[]{0.0025f, 0.9975f}, new int[]{50, 51}),
	Cr 	("Cr", 	24, 51.996100f, 	new float[]{49.9460442f, 51.9405075f, 52.9406494f, 53.9388804f}, new float[]{0.04345f, 0.83789f, 0.09501f, 0.02365f}, new int[]{50, 52, 53, 54}),
	Mn 	("Mn", 	25, 54.938045f, 	new float[]{54.9380451f}, new float[]{1.0f}, new int[]{55}),
	Fe 	("Fe", 	26, 55.845000f, 	new float[]{53.9396105f, 55.9349375f, 56.935394f, 57.9332756f}, new float[]{0.05845f, 0.91754f, 0.02119f, 0.00282f}, new int[]{54, 56, 57, 58}),
	Co 	("Co", 	27, 58.933195f, 	new float[]{58.933195f}, new float[]{1.0f}, new int[]{59}),
	Ni 	("Ni", 	28, 58.693400f, 	new float[]{57.9353429f, 59.9307864f, 60.931056f, 61.9283451f, 63.927966f}, new float[]{0.680769f, 0.262231f, 0.011399f, 0.036345f, 0.009256f}, new int[]{58, 60, 61, 62, 64}),
	Cu 	("Cu", 	29, 63.546000f, 	new float[]{62.9295975f, 64.9277895f}, new float[]{0.6915f, 0.3085f}, new int[]{63, 65}),
	Zn 	("Zn", 	30, 65.380000f, 	new float[]{63.9291422f, 65.9260334f, 66.9271273f, 67.9248442f, 69.9253193f}, new float[]{0.48268f, 0.27975f, 0.04102f, 0.19024f, 0.00631f}, new int[]{64, 66, 67, 68, 70}),
	Ga 	("Ga", 	31, 69.723000f, 	new float[]{68.9255736f, 70.9247013f}, new float[]{0.60108f, 0.39892f}, new int[]{69, 71}),
	Ge 	("Ge", 	32, 72.640000f, 	new float[]{69.9242474f, 71.9220758f, 72.9234589f, 73.9211778f, 75.9214026f}, new float[]{0.2038f, 0.2731f, 0.0776f, 0.3672f, 0.0783f}, new int[]{70, 72, 73, 74, 76}),
	As 	("As", 	33, 74.921600f, 	new float[]{74.9215965f}, new float[]{1.0f}, new int[]{75}),
	Se 	("Se", 	34, 78.960000f, 	new float[]{73.9224764f, 75.9192136f, 76.919914f, 77.9173091f, 79.9165213f, 81.9166994f}, new float[]{0.0089f, 0.0937f, 0.0763f, 0.2377f, 0.4961f, 0.0873f}, new int[]{74, 76, 77, 78, 80, 82}),
	Br 	("Br", 	35, 79.904000f, 	new float[]{78.9183371f, 80.9162906f}, new float[]{0.5069f, 0.4931f}, new int[]{79, 81}),
	Kr 	("Kr", 	36, 83.798000f, 	new float[]{77.9203648f, 79.916379f, 81.9134836f, 82.914136f, 83.911507f, 85.91061073f}, new float[]{0.00355f, 0.02286f, 0.11593f, 0.115f, 0.56987f, 0.17279f}, new int[]{78, 80, 82, 83, 84, 86}),
	Rb 	("Rb", 	37, 85.467800f, 	new float[]{84.911789738f, 86.909180527f}, new float[]{0.7217f, 0.2783f}, new int[]{85, 87}),
	Sr 	("Sr", 	38, 87.620000f, 	new float[]{83.913425f, 85.9092602f, 86.9088771f, 87.9056121f}, new float[]{0.0056f, 0.0986f, 0.07f, 0.8258f}, new int[]{84, 86, 87, 88}),
	Y 	("Y", 	39, 88.905850f, 	new float[]{88.9058483f}, new float[]{1.0f}, new int[]{89}),
	Zr 	("Zr", 	40, 91.224000f, 	new float[]{89.9047044f, 90.9056458f, 91.9050408f, 93.9063152f, 95.9082734f}, new float[]{0.5145f, 0.1122f, 0.1715f, 0.1738f, 0.028f}, new int[]{90, 91, 92, 94, 96}),
	Nb 	("Nb", 	41, 92.906380f, 	new float[]{92.9063781f}, new float[]{1.0f}, new int[]{93}),
	Mo 	("Mo", 	42, 95.960000f, 	new float[]{91.906811f, 93.9050883f, 94.9058421f, 95.9046795f, 96.9060215f, 97.9054082f, 99.907477f}, new float[]{0.1477f, 0.0923f, 0.159f, 0.1668f, 0.0956f, 0.2419f, 0.0967f}, new int[]{92, 94, 95, 96, 97, 98, 100}),
	Tc 	("Tc", 	43, 98.000000f, 	new float[]{96.906365f, 97.907216f, 98.9062547f}, new float[]{0.0f, 0.0f, 0.0f}, new int[]{97, 98, 99}),
	Ru 	("Ru", 	44, 101.070000f, new float[]{95.907598f, 97.905287f, 98.9059393f, 99.9042195f, 100.9055821f, 101.9043493f, 103.905433f}, new float[]{0.0554f, 0.0187f, 0.1276f, 0.126f, 0.1706f, 0.3155f, 0.1862f}, new int[]{96, 98, 99, 100, 101, 102, 104}),
	Rh 	("Rh", 	45, 102.905500f, new float[]{102.905504f}, new float[]{1.0f}, new int[]{103}),
	Pd 	("Pd", 	46, 106.420000f, new float[]{101.905609f, 103.904036f, 104.905085f, 105.903486f, 107.903892f, 109.905153f}, new float[]{0.0102f, 0.1114f, 0.2233f, 0.2733f, 0.2646f, 0.1172f}, new int[]{102, 104, 105, 106, 108, 110}),
	Ag 	("Ag", 	47, 107.868200f, new float[]{106.905097f, 108.904752f}, new float[]{0.51839f, 0.48161f}, new int[]{107, 109}),
	Cd 	("Cd", 	48, 112.411000f, new float[]{105.906459f, 107.904184f, 109.9030021f, 110.9041781f, 111.9027578f, 112.9044017f, 113.9033585f, 115.904756f}, new float[]{0.0125f, 0.0089f, 0.1249f, 0.128f, 0.2413f, 0.1222f, 0.2873f, 0.0749f}, new int[]{106, 108, 110, 111, 112, 113, 114, 116}),
	In 	("In", 	49, 114.818000f, new float[]{112.904058f, 114.903878f}, new float[]{0.0429f, 0.9571f}, new int[]{113, 115}),
	Sn 	("Sn", 	50, 118.710000f, new float[]{111.904818f, 113.902779f, 114.903342f, 115.901741f, 116.902952f, 117.901603f, 118.903308f, 119.9021947f, 121.903439f, 123.9052739f}, new float[]{0.0097f, 0.0066f, 0.0034f, 0.1454f, 0.0768f, 0.2422f, 0.0859f, 0.3258f, 0.0463f, 0.0579f}, new int[]{112, 114, 115, 116, 117, 118, 119, 120, 122, 124}),
	Sb 	("Sb", 	51, 121.760000f, new float[]{120.9038157f, 122.904214f}, new float[]{0.5721f, 0.4279f}, new int[]{121, 123}),
	Te 	("Te", 	52, 127.600000f, new float[]{119.90402f, 121.9030439f, 122.90427f, 123.9028179f, 124.9044307f, 125.9033117f, 127.9044631f, 129.9062244f}, new float[]{0.0009f, 0.0255f, 0.0089f, 0.0474f, 0.0707f, 0.1884f, 0.3174f, 0.3408f}, new int[]{120, 122, 123, 124, 125, 126, 128, 130}),
	I 	("I", 	53, 126.904470f, new float[]{126.904473f}, new float[]{1.0f}, new int[]{127}),
	Xe 	("Xe", 	54, 131.293000f, new float[]{123.905893f, 125.904274f, 127.9035313f, 128.9047794f, 129.903508f, 130.9050824f, 131.9041535f, 133.9053945f, 135.907219f}, new float[]{0.000952f, 0.00089f, 0.019102f, 0.264006f, 0.04071f, 0.212324f, 0.269086f, 0.104357f, 0.088573f}, new int[]{124, 126, 128, 129, 130, 131, 132, 134, 136}),
	Cs 	("Cs", 	55, 132.905452f, new float[]{132.905451933f}, new float[]{1.0f}, new int[]{133}),
	Ba 	("Ba", 	56, 137.327000f, new float[]{129.9063208f, 131.9050613f, 133.9045084f, 134.9056886f, 135.9045759f, 136.9058274f, 137.9052472f}, new float[]{0.00106f, 0.00101f, 0.02417f, 0.06592f, 0.07854f, 0.11232f, 0.71698f}, new int[]{130, 132, 134, 135, 136, 137, 138}),
	La 	("La", 	57, 138.905470f, new float[]{137.907112f, 138.9063533f}, new float[]{0.0009f, 0.9991f}, new int[]{138, 139}),
	Ce 	("Ce", 	58, 140.116000f, new float[]{135.907172f, 137.905991f, 139.9054387f, 141.909244f}, new float[]{0.00185f, 0.00251f, 0.8845f, 0.11114f}, new int[]{136, 138, 140, 142}),
	Pr 	("Pr", 	59, 140.907650f, new float[]{140.9076528f}, new float[]{1.0f}, new int[]{141}),
	Nd 	("Nd", 	60, 144.242000f, new float[]{141.9077233f, 142.9098143f, 143.9100873f, 144.9125736f, 145.9131169f, 147.916893f, 149.920891f}, new float[]{0.272f, 0.122f, 0.238f, 0.083f, 0.172f, 0.057f, 0.056f}, new int[]{142, 143, 144, 145, 146, 148, 150}),
	Pm 	("Pm", 	61, 145.000000f, new float[]{144.912749f, 146.9151385f}, new float[]{0.0f, 0.0f}, new int[]{145, 147}),
	Sm 	("Sm", 	62, 150.360000f, new float[]{143.911999f, 146.9148979f, 147.9148227f, 148.9171847f, 149.9172755f, 151.9197324f, 153.9222093f}, new float[]{0.0307f, 0.1499f, 0.1124f, 0.1382f, 0.0738f, 0.2675f, 0.2275f}, new int[]{144, 147, 148, 149, 150, 152, 154}),
	Eu 	("Eu", 	63, 151.964000f, new float[]{150.9198502f, 152.9212303f}, new float[]{0.4781f, 0.5219f}, new int[]{151, 153}),
	Gd 	("Gd", 	64, 157.250000f, new float[]{151.919791f, 153.9208656f, 154.922622f, 155.9221227f, 156.9239601f, 157.9241039f, 159.9270541f}, new float[]{0.002f, 0.0218f, 0.148f, 0.2047f, 0.1565f, 0.2484f, 0.2186f}, new int[]{152, 154, 155, 156, 157, 158, 160}),
	Tb 	("Tb", 	65, 158.925350f, new float[]{158.9253468f}, new float[]{1.0f}, new int[]{159}),
	Dy 	("Dy", 	66, 162.500000f, new float[]{155.924283f, 157.924409f, 159.9251975f, 160.9269334f, 161.9267984f, 162.9287312f, 163.9291748f}, new float[]{0.00056f, 0.00095f, 0.02329f, 0.18889f, 0.25475f, 0.24896f, 0.2826f}, new int[]{156, 158, 160, 161, 162, 163, 164}),
	Ho 	("Ho", 	67, 164.930320f, new float[]{164.9303221f}, new float[]{1.0f}, new int[]{165}),
	Er 	("Er", 	68, 167.259000f, new float[]{161.928778f, 163.9292f, 165.9302931f, 166.9320482f, 167.9323702f, 169.9354643f}, new float[]{0.00139f, 0.01601f, 0.33503f, 0.22869f, 0.26978f, 0.1491f}, new int[]{162, 164, 166, 167, 168, 170}),
	Tm 	("Tm", 	69, 168.934210f, new float[]{168.9342133f}, new float[]{1.0f}, new int[]{169}),
	Yb 	("Yb", 	70, 173.054000f, new float[]{167.933897f, 169.9347618f, 170.9363258f, 171.9363815f, 172.9382108f, 173.9388621f, 175.9425717f}, new float[]{0.0013f, 0.0304f, 0.1428f, 0.2183f, 0.1613f, 0.3183f, 0.1276f}, new int[]{168, 170, 171, 172, 173, 174, 176}),
	Lu 	("Lu", 	71, 174.966800f, new float[]{174.9407718f, 175.9426863f}, new float[]{0.9741f, 0.0259f}, new int[]{175, 176}),
	Hf 	("Hf", 	72, 178.490000f, new float[]{173.940046f, 175.9414086f, 176.9432207f, 177.9436988f, 178.9458161f, 179.94655f}, new float[]{0.0016f, 0.0526f, 0.186f, 0.2728f, 0.1362f, 0.3508f}, new int[]{174, 176, 177, 178, 179, 180}),
	Ta 	("Ta", 	73, 180.947880f, new float[]{179.9474648f, 180.9479958f}, new float[]{0.00012f, 0.99988f}, new int[]{180, 181}),
	W 	("W", 	74, 183.840000f, new float[]{179.946704f, 181.9482042f, 182.950223f, 183.9509312f, 185.9543641f}, new float[]{0.0012f, 0.265f, 0.1431f, 0.3064f, 0.2843f}, new int[]{180, 182, 183, 184, 186}),
	Re 	("Re", 	75, 186.207000f, new float[]{184.952955f, 186.9557531f}, new float[]{0.374f, 0.626f}, new int[]{185, 187}),
	Os 	("Os", 	76, 190.230000f, new float[]{183.9524891f, 185.9538382f, 186.9557505f, 187.9558382f, 188.9581475f, 189.958447f, 191.9614807f}, new float[]{0.0002f, 0.0159f, 0.0196f, 0.1324f, 0.1615f, 0.2626f, 0.4078f}, new int[]{184, 186, 187, 188, 189, 190, 192}),
	Ir 	("Ir", 	77, 192.217000f, new float[]{190.960594f, 192.9629264f}, new float[]{0.373f, 0.627f}, new int[]{191, 193}),
	Pt 	("Pt", 	78, 195.084000f, new float[]{189.959932f, 191.961038f, 193.9626803f, 194.9647911f, 195.9649515f, 197.967893f}, new float[]{0.00014f, 0.00782f, 0.32967f, 0.33832f, 0.25242f, 0.07163f}, new int[]{190, 192, 194, 195, 196, 198}),
	Au 	("Au", 	79, 196.966569f, new float[]{196.9665687f}, new float[]{1.0f}, new int[]{197}),
	Hg 	("Hg", 	80, 200.590000f, new float[]{195.965833f, 197.966769f, 198.9682799f, 199.968326f, 200.9703023f, 201.970643f, 203.9734939f}, new float[]{0.0015f, 0.0997f, 0.1687f, 0.231f, 0.1318f, 0.2986f, 0.0687f}, new int[]{196, 198, 199, 200, 201, 202, 204}),
	Tl 	("Tl", 	81, 204.383300f, new float[]{202.9723442f, 204.9744275f}, new float[]{0.2952f, 0.7048f}, new int[]{203, 205}),
	Pb 	("Pb", 	82, 207.200000f, new float[]{203.9730436f, 205.9744653f, 206.9758969f, 207.9766521f}, new float[]{0.014f, 0.241f, 0.221f, 0.524f}, new int[]{204, 206, 207, 208}),
	Bi 	("Bi", 	83, 208.980400f, new float[]{208.9803987f}, new float[]{1.0f}, new int[]{209}),
	Po 	("Po", 	84, 209.000000f, new float[]{208.9824304f, 209.9828737f}, new float[]{0.0f, 0.0f}, new int[]{209, 210}),
	At 	("At", 	85, 210.000000f, new float[]{209.987148f, 210.9874963f}, new float[]{0.0f, 0.0f}, new int[]{210, 211}),
	Rn 	("Rn", 	86, 222.000000f, new float[]{210.990601f, 220.011394f, 222.0175777f}, new float[]{0.0f, 0.0f, 0.0f}, new int[]{211, 220, 222}),
	Fr 	("Fr", 	87, 223.000000f, new float[]{223.0197359f}, new float[]{0.0f}, new int[]{223}),
	Ra 	("Ra", 	88, 226.000000f, new float[]{223.0185022f, 224.0202118f, 226.0254098f, 228.0310703f}, new float[]{0.0f, 0.0f, 0.0f, 0.0f}, new int[]{223, 224, 226, 228}),
	Ac 	("Ac", 	89, 227.000000f, new float[]{227.0277521f}, new float[]{0.0f}, new int[]{227}),
	Th 	("Th", 	90, 232.038060f, new float[]{230.0331338f, 232.0380553f}, new float[]{0.0f, 1.0f}, new int[]{230, 232}),
	Pa 	("Pa", 	91, 231.035880f, new float[]{231.035884f}, new float[]{1.0f}, new int[]{231}),
	U 	("U", 	92, 238.028910f, new float[]{233.0396352f, 234.0409521f, 235.0439299f, 236.045568f, 238.0507882f}, new float[]{0.0f, 5.4e-05f, 0.007204f, 0.0f, 0.992742f}, new int[]{233, 234, 235, 236, 238}),
	Np 	("Np", 	93, 237.000000f, new float[]{236.04657f, 237.0481734f}, new float[]{0.0f, 0.0f}, new int[]{236, 237}),
	Pu 	("Pu", 	94, 244.000000f, new float[]{238.0495599f, 239.0521634f, 240.0538135f, 241.0568515f, 242.0587426f, 244.064204f}, new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}, new int[]{238, 239, 240, 241, 242, 244}),
	Am 	("Am", 	95, 243.000000f, new float[]{241.0568291f, 243.0613811f}, new float[]{0.0f, 0.0f}, new int[]{241, 243}),
	Cm 	("Cm", 	96, 247.000000f, new float[]{243.0613891f, 244.0627526f, 245.0654912f, 246.0672237f, 247.070354f, 248.072349f}, new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}, new int[]{243, 244, 245, 246, 247, 248}),
	Bk 	("Bk", 	97, 247.000000f, new float[]{247.070307f, 249.0749867f}, new float[]{0.0f, 0.0f}, new int[]{247, 249}),
	Cf 	("Cf", 	98, 251.000000f, new float[]{249.0748535f, 250.0764061f, 251.079587f, 252.081626f}, new float[]{0.0f, 0.0f, 0.0f, 0.0f}, new int[]{249, 250, 251, 252}),
	Es 	("Es", 	99, 252.000000f, new float[]{252.08298f}, new float[]{0.0f}, new int[]{252}),
	Fm 	("Fm", 	100, 257.000000f, new float[]{257.095105f}, new float[]{0.0f}, new int[]{257}),
	Md 	("Md", 	101, 258.000000f, new float[]{258.098431f, 260.10365f}, new float[]{0.0f, 0.0f}, new int[]{258, 260}),
	No 	("No", 	102, 259.000000f, new float[]{259.10103f}, new float[]{0.0f}, new int[]{259}),
	Lr 	("Lr", 	103, 262.000000f, new float[]{262.10963f}, new float[]{0.0f}, new int[]{262}),
	Rf 	("Rf", 	104, 265.000000f, new float[]{265.1167f}, new float[]{0.0f}, new int[]{265}),
	Db 	("Db", 	105, 268.000000f, new float[]{268.12545f}, new float[]{0.0f}, new int[]{268}),
	Sg 	("Sg", 	106, 271.000000f, new float[]{271.13347f}, new float[]{0.0f}, new int[]{271}),
	Bh 	("Bh", 	107, 272.000000f, new float[]{272.13803f}, new float[]{0.0f}, new int[]{272}),
	Hs 	("Hs", 	108, 270.000000f, new float[]{270.13465f}, new float[]{0.0f}, new int[]{270}),
	Mt 	("Mt", 	109, 276.000000f, new float[]{276.15116f}, new float[]{0.0f}, new int[]{276}),
	Ds 	("Ds", 	110, 281.000000f, new float[]{281.16206f}, new float[]{0.0f}, new int[]{281}),
	Rg 	("Rg", 	111, 280.000000f, new float[]{280.16447f}, new float[]{0.0f}, new int[]{280}),
	Cn 	("Cn", 	112, 285.000000f, new float[]{285.17411f}, new float[]{0.0f}, new int[]{285}),
	Uut ("Uut", 113, 284.000000f, new float[]{284.17808f}, new float[]{0.0f}, new int[]{284}),
	Uuq ("Uuq", 114, 289.000000f, new float[]{289.18728f}, new float[]{0.0f}, new int[]{289}),
	Uup ("Uup", 115, 288.000000f, new float[]{288.19249f}, new float[]{0.0f}, new int[]{288}),
	Uuh ("Uuh", 116, 293.000000f, new float[]{0.0f}, new float[]{0.0f}, new int[]{293}),
	Uus ("Uus", 117, 292.000000f, new float[]{292.20755f}, new float[]{0.0f}, new int[]{292}),
	Uuo ("Uuo", 118, 294.000000f, new float[]{0.0f}, new float[]{0.0f}, new int[]{294});

	public final String	symbol;
	public final int 		atomicNumber;
	public final float	standardAtomicWeight;
	public final float[]	isotopeRelativeAtomicMasses;
	public final float[]	isotopeOccurence;
	public final int[]	isotopeMassNumbers;
	public final float 	monoisotopicWeight;
	public final IsotopeDistribution isotopeDistribution;
	
	Element(
			String symbol, 
			int atomicNumber,
			float standardAtomicWeight,
			float[] isotopeRelativeAtomicMasses,
			float[] isotopeOccurence, 
			int[] isotopeMassNumbers
	) {
		this.symbol = symbol;
		this.atomicNumber = atomicNumber;
		this.standardAtomicWeight = standardAtomicWeight;
		this.isotopeRelativeAtomicMasses = isotopeRelativeAtomicMasses;
		this.isotopeOccurence = isotopeOccurence;
		this.isotopeMassNumbers = isotopeMassNumbers;
		
		float mostFrequentOccurence = isotopeOccurence[0];
		float mostFrequentMass = isotopeRelativeAtomicMasses[0];
		for (int i = 1; i < isotopeMassNumbers.length; i++)
			if (isotopeOccurence[i] > mostFrequentOccurence) {
				mostFrequentOccurence = isotopeOccurence[i];
				mostFrequentMass = isotopeRelativeAtomicMasses[i];
			}
		monoisotopicWeight = mostFrequentMass;
		
		int low = Math.round(isotopeRelativeAtomicMasses[0]);
		int high = Math.round(isotopeRelativeAtomicMasses[isotopeRelativeAtomicMasses.length - 1]);
		isotopeDistribution = new IsotopeDistribution();
		isotopeDistribution.m0 = low;
		isotopeDistribution.dm = high - low + 1;
		isotopeDistribution.intensities = new float[isotopeDistribution.dm];
		for (int i = 0; i < isotopeOccurence.length; i++)
			isotopeDistribution.intensities[Math.round(isotopeRelativeAtomicMasses[i]) - low] = isotopeOccurence[i];
	}
	
	public static Element fromString(String str) {
		Element[] es = Element.values();
		for (Element e : es)
			if (e.symbol.equals(str))
				return e;
		return null;
	}
}
