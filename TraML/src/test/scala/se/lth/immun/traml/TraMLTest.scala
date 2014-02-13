package se.lth.immun.traml

import org.scalatest.junit.AssertionsForJUnit
import scala.collection.mutable.ListBuffer
import org.junit.Assert._
import org.junit.Test
import org.junit.Before

import java.io.StringReader
import se.lth.immun.xml.XmlReader

class TraMLTest {
	
	val TRA_ML_FILE = 
"""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<TraML xsi:schemaLocation="http://psi.hupo.org/ms/traml TraML0.9.5.xsd" xmlns="http://psi.hupo.org/ms/traml" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <cvList>
        <cv id="MS" fullName="Proteomics Standards Initiative Mass Spectrometry Ontology" URI="http://psidev.cvs.sourceforge.net/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo"/>
        <cv id="UO" fullName="Unit Ontology" URI="http://obo.cvs.sourceforge.net/obo/obo/ontology/phenotype/unit.obo"/>
        <cv id="UNIMOD" fullName="UNIMOD CV for modifications" URI="http://www.unimod.org/obo/unimod.obo"/>
    </cvList>
    <SourceFileList>
        <SourceFile location="C:\Tomcat-7.0\temp\1318259578903-0" name="TSQ_1832_1b_40.csv" id="ThermoTSQ_to_traml_converter_v0.9.1"/>
    </SourceFileList>
    <InstrumentList>
        <Instrument id="1">
            <cvParam value="Positive" accession="MS:1000037" name="polarity" cvRef="MS"/>
        </Instrument>
    </InstrumentList>
    <CompoundList>
        <Peptide sequence="AAQVAQDEEIAR" id="AAQVAQDEEIAR.2">
            <RetentionTimeList>
                <RetentionTime>
                    <cvParam unitCvRef="UO" unitName="minute" unitAccession="UO:0000031" value="18.61" accession="MS:1000916" name="retention time window lower offset" cvRef="MS"/>
                    <cvParam unitCvRef="UO" unitName="minute" unitAccession="UO:0000031" value="28.61" accession="MS:1000917" name="retention time window upper offset" cvRef="MS"/>
                </RetentionTime>
            </RetentionTimeList>
        </Peptide>
        <Peptide sequence="AEFSAGAWSEPR" id="AEFSAGAWSEPR.2">
            <RetentionTimeList>
                <RetentionTime>
                    <cvParam unitCvRef="UO" unitName="minute" unitAccession="UO:0000031" value="31.68" accession="MS:1000916" name="retention time window lower offset" cvRef="MS"/>
                    <cvParam unitCvRef="UO" unitName="minute" unitAccession="UO:0000031" value="41.68" accession="MS:1000917" name="retention time window upper offset" cvRef="MS"/>
                </RetentionTime>
            </RetentionTimeList>
        </Peptide>
	</CompoundList>
    <TransitionList>
        <Transition id="AAQVAQDEEIAR.2y8-1" peptideRef="AAQVAQDEEIAR.2">
            <Precursor>
                <cvParam unitCvRef="MS" unitName="m/z" unitAccession="MS:1000040" value="650.8288" accession="MS:1000827" name="isolation window target m/z" cvRef="MS"/>
            </Precursor>
            <Product>
                <cvParam unitCvRef="MS" unitName="m/z" unitAccession="MS:1000040" value="931.4486" accession="MS:1000827" name="isolation window target m/z" cvRef="MS"/>
                <ConfigurationList>
                    <Configuration>
                        <cvParam unitCvRef="UO" unitName="electronvolt" unitAccession="UO:0000266" value="25.4" accession="MS:1000045" name="collision energy" cvRef="MS"/>
                    </Configuration>
                </ConfigurationList>
            </Product>
        </Transition>
        <Transition id="AAQVAQDEEIAR.2y9-1" peptideRef="AAQVAQDEEIAR.2">
            <Precursor>
                <cvParam unitCvRef="MS" unitName="m/z" unitAccession="MS:1000040" value="650.8288" accession="MS:1000827" name="isolation window target m/z" cvRef="MS"/>
            </Precursor>
            <Product>
                <cvParam unitCvRef="MS" unitName="m/z" unitAccession="MS:1000040" value="1030.5169" accession="MS:1000827" name="isolation window target m/z" cvRef="MS"/>
                <ConfigurationList>
                    <Configuration>
                        <cvParam unitCvRef="UO" unitName="electronvolt" unitAccession="UO:0000266" value="25.4" accession="MS:1000045" name="collision energy" cvRef="MS"/>
                    </Configuration>
                </ConfigurationList>
            </Product>
        </Transition>
		<Transition id="AEFSAGAWSEPR.2y7-1" peptideRef="AEFSAGAWSEPR.2">
            <Precursor>
                <cvParam unitCvRef="MS" unitName="m/z" unitAccession="MS:1000040" value="654.3049" accession="MS:1000827" name="isolation window target m/z" cvRef="MS"/>
            </Precursor>
            <Product>
                <cvParam unitCvRef="MS" unitName="m/z" unitAccession="MS:1000040" value="802.3838" accession="MS:1000827" name="isolation window target m/z" cvRef="MS"/>
                <ConfigurationList>
                    <Configuration>
                        <cvParam unitCvRef="UO" unitName="electronvolt" unitAccession="UO:0000266" value="25.6" accession="MS:1000045" name="collision energy" cvRef="MS"/>
                    </Configuration>
                </ConfigurationList>
            </Product>
        </Transition>
        <Transition id="AEFSAGAWSEPR.2y9-1" peptideRef="AEFSAGAWSEPR.2">
            <Precursor>
                <cvParam unitCvRef="MS" unitName="m/z" unitAccession="MS:1000040" value="654.3049" accession="MS:1000827" name="isolation window target m/z" cvRef="MS"/>
            </Precursor>
            <Product>
                <cvParam unitCvRef="MS" unitName="m/z" unitAccession="MS:1000040" value="960.4529" accession="MS:1000827" name="isolation window target m/z" cvRef="MS"/>
                <ConfigurationList>
                    <Configuration>
                        <cvParam unitCvRef="UO" unitName="electronvolt" unitAccession="UO:0000266" value="25.6" accession="MS:1000045" name="collision energy" cvRef="MS"/>
                    </Configuration>
                </ConfigurationList>
            </Product>
        </Transition>
    </TransitionList>
</TraML>
"""
		
	val TOY_EXAMPLE_TRAML = """
<?xml version="1.0" encoding="UTF-8"?>
<TraML version="0.9.5" xmlns="http://psi.hupo.org/ms/traml" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://psi.hupo.org/ms/traml TraML0.9.5.xsd">
  <cvList>
    <cv id="MS" fullName="Proteomics Standards Initiative Mass Spectrometry Ontology" version="2.31.0" URI="http://psidev.cvs.sourceforge.net/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo"/>
    <cv id="UO" fullName="Unit Ontology" version="unknown" URI="http://obo.cvs.sourceforge.net/obo/obo/ontology/phenotype/unit.obo"/>
    <cv id="UNIMOD" fullName="UNIMOD CV for modifications" version="unknown" URI="http://www.unimod.org/obo/unimod.obo"/>
  </cvList>

  <SourceFileList>
    <SourceFile id="sf1" name="OneTransition.tsv" location="file:///F:/data/Exp01">
      <cvParam cvRef="MS" accession="MS:1000914" name="tab delimited text file" value=""/>
      <cvParam cvRef="MS" accession="MS:1000569" name="SHA-1" value="71be39fb2700ab2f3c8b2234b91274968b6899b1"/>
    </SourceFile>
  </SourceFileList>

  <ContactList>
    <Contact id="CS">
      <cvParam cvRef="MS" accession="MS:1000586" name="contact name" value="Eric Deutsch"/>
      <cvParam cvRef="MS" accession="MS:1000590" name="contact organization" value="Institute for Systems Biology"/>
      <cvParam cvRef="MS" accession="MS:1000587" name="contact address" value="1441 NE 34th St, Seattle WA 98103, USA"/>
      <cvParam cvRef="MS" accession="MS:1000588" name="contact URL" value="http://www.systemsbiology.org/"/>
      <cvParam cvRef="MS" accession="MS:1000589" name="contact email" value="example@systemsbiology.org"/>
    </Contact>
  </ContactList>

  <PublicationList>
    <Publication id="PMID12748199">
      <cvParam cvRef="MS" accession="MS:1000879" name="PubMed identifier" value="12748199"/>
    </Publication>
  </PublicationList>

  <InstrumentList>
    <Instrument id="LCQ_Deca">
      <cvParam cvRef="MS" accession="MS:1000554" name="LCQ Deca"/>
    </Instrument>
    <Instrument id="QTRAP">
      <cvParam cvRef="MS" accession="MS:1000870" name="4000 QTRAP"/>
    </Instrument>
  </InstrumentList>

  <SoftwareList>
    <Software id="MaRiMba" version="1.0">
      <cvParam cvRef="MS" accession="MS:1000872" name="MaRiMba"/>
    </Software>
    <Software id="SSRCalc3.0" version="3.0">
      <cvParam cvRef="MS" accession="MS:1000874" name="SSRCalc"/>
    </Software>
    <Software id="Skyline0.5" version="0.5">
      <cvParam cvRef="MS" accession="MS:1000922" name="Skyline"/>
    </Software>
  </SoftwareList>

  <!-- Note that the protein names, peptide sequences, modification and transition values are not all fully internally consistent, but are intended merely as examples here -->
  <ProteinList>
    <Protein id="Q12149">
      <cvParam cvRef="MS" accession="MS:1000885" name="protein accession" value="Q00613"/>
      <cvParam cvRef="MS" accession="MS:1000883" name="protein short name" value="HSF 1"/>
      <cvParam cvRef="MS" accession="MS:1000886" name="protein name" value="Heat shock factor protein 1"/>
      <Sequence>MSTEMETKAEDVETFAFQAEIAQLMSLIINTFYSNKEIFLRELISNSSDALDKIRYESLTDPSKLDNGKE</Sequence>
    </Protein>
    <Protein id="ENSP00000332698">
      <cvParam cvRef="MS" accession="MS:1000885" name="protein accession" value="ENSP00000332698"/>
      <cvParam cvRef="MS" accession="MS:1000883" name="protein short name" value="HSF 1"/>
      <cvParam cvRef="MS" accession="MS:1000886" name="protein name" value="Heat shock factor protein 1"/>
      <Sequence>MSTEMETKAEDVETFAFQAEIAQLMSLIINTFYSNKEIFLRELISNSSDALDKIRYESLTDPSKLDNGKEELISNSSDALDKI</Sequence>
    </Protein>
  </ProteinList>

  <CompoundList>
    <Peptide id="ADTHFLLNIYDQLR-M1" sequence="ADTHFLLNIYDQLR">
      <cvParam cvRef="MS" accession="MS:1000891" name="heavy labeled peptide"/>
      <cvParam cvRef="MS" accession="MS:1000893" name="peptide group label" value="G1"/>
      <cvParam cvRef="MS" accession="MS:1000863" name="predicted isoelectric point" value="5.22"/>
      <cvParam cvRef="MS" accession="MS:1001117" name="theoretical mass" value="1189.22" unitCvRef="UO" unitAccession="UO:0000221" unitName="dalton"/>
      <ProteinRef ref="Q12149"/>
      <ProteinRef ref="ENSP00000332698"/>
      <Modification location="0" monoisotopicMassDelta="127.063324">
        <cvParam cvRef="UNIMOD" accession="UNIMOD:29" name="SMA"/>
      </Modification>
      <Modification location="1" monoisotopicMassDelta="15.994919">
        <cvParam cvRef="UNIMOD" accession="UNIMOD:35" name="Oxidation"/>
      </Modification>
      <RetentionTimeList>
        <RetentionTime>
          <cvParam unitCvRef="UO" unitName="minute" unitAccession="UO:0000031" value="31.68" accession="MS:1000916" name="retention time window lower offset" cvRef="MS"/>
          <cvParam unitCvRef="UO" unitName="minute" unitAccession="UO:0000031" value="41.68" accession="MS:1000917" name="retention time window upper offset" cvRef="MS"/>
		</RetentionTime>
      </RetentionTimeList>
      <Evidence>
        <cvParam cvRef="MS" accession="MS:1001100" name="confident peptide" value="6"/>
      </Evidence>
    </Peptide>
    <Peptide id="PEPTIDEC" sequence="PEPTIDEC"/>
    <Peptide id="PEPTIDEM" sequence="PEPTIDEM"/>
    <Compound id="glyoxylate">
      <cvParam cvRef="MS" accession="MS:1001117" name="theoretical mass" value="423.39" unitCvRef="UO" unitAccession="UO:0000221" unitName="dalton"/>
      <cvParam cvRef="MS" accession="MS:1000866" name="molecular formula" value="C2HO3"/>
      <cvParam cvRef="MS" accession="MS:1000868" name="SMILES string" value="[CH](=[O])[C](=[O])[O-]"/>
      <RetentionTimeList>
        <RetentionTime>
          <cvParam cvRef="MS" accession="MS:1000896" name="normalized retention time" value="22.34" unitCvRef="UO" unitAccession="UO:0000031" unitName="minute"/>
          <cvParam cvRef="MS" accession="MS:1000902" name="H-PINS retention time normalization standard"/>
        </RetentionTime>
      </RetentionTimeList>
    </Compound>
  </CompoundList>

  <TransitionList>
    <Transition id="ADTHFLLNIYDQLR-M1-T1" peptideRef="ADTHFLLNIYDQLR-M1">
      <Precursor>
        <cvParam cvRef="MS" accession="MS:1000827" name="isolation window target m/z" value="862.9467" unitCvRef="MS" unitAccession="MS:1000040" unitName="m/z"/>
        <cvParam cvRef="MS" accession="MS:1000041" name="charge state" value="2"/>
      </Precursor>
      <Product>
        <cvParam cvRef="MS" accession="MS:1000827" name="isolation window target m/z" value="1040.57" unitCvRef="MS" unitAccession="MS:1000040" unitName="m/z"/>
        <cvParam cvRef="MS" accession="MS:1000041" name="charge state" value="1"/>
        <InterpretationList>
          <Interpretation>
	    <cvParam cvRef="MS" accession="MS:1000926" name="product interpretation rank" value="1"/>
	    <cvParam cvRef="MS" accession="MS:1001220" name="frag: y ion"/>
	    <cvParam cvRef="MS" accession="MS:1000903" name="product ion series ordinal" value="8"/>
	    <cvParam cvRef="MS" accession="MS:1000904" name="product ion m/z delta" value="0.03" unitCvRef="MS" unitAccession="MS:1000040" unitName="m/z"/>
          </Interpretation>
          <Interpretation>
	    <cvParam cvRef="MS" accession="MS:1000926" name="product interpretation rank" value="2"/>
	    <cvParam cvRef="MS" accession="MS:1001222" name="frag: b ion - H2O"/>
	    <cvParam cvRef="MS" accession="MS:1000903" name="product ion series ordinal" value="9"/>
	    <cvParam cvRef="MS" accession="MS:1000904" name="product ion m/z delta" value="-0.43" unitCvRef="MS" unitAccession="MS:1000040" unitName="m/z"/>
          </Interpretation>
        </InterpretationList>
        <ConfigurationList>
          <Configuration instrumentRef="QTRAP" contactRef="CS">
            <cvParam cvRef="MS" accession="MS:1000502" name="dwell time" value="0.12" unitCvRef="UO" unitAccession="UO:0000010" unitName="second"/>
            <cvParam cvRef="MS" accession="MS:1000045" name="collision energy" value="26" unitCvRef="UO" unitAccession="UO:0000266" unitName="electronvolt"/>
            <cvParam cvRef="MS" accession="MS:1000875" name="declustering potential" value="64" unitCvRef="UO" unitAccession="UO:0000218" unitName="volt"/>
            <cvParam cvRef="MS" accession="MS:1000419" name="collision gas" value="argon"/>
            <cvParam cvRef="MS" accession="MS:1000869" name="collision gas pressure" value="12" unitCvRef="UO" unitAccession="UO:0000110" unitName="pascal"/>
            <cvParam cvRef="MS" accession="MS:1000876" name="cone voltage" value="1200" unitCvRef="UO" unitAccession="UO:0000218" unitName="volt"/>
            <cvParam cvRef="MS" accession="MS:1000880" name="interchannel delay" value="0.01" unitCvRef="UO" unitAccession="UO:0000010" unitName="second"/>
            <cvParam cvRef="MS" accession="MS:1000877" name="tube lens" value="23" unitCvRef="UO" unitAccession="UO:0000218" unitName="volt"/>

            <ValidationStatus>
	      <cvParam cvRef="MS" accession="MS:1000910" name="transition optimized on specified instrument"/>
	      <cvParam cvRef="MS" accession="MS:1000139" name="4000 Q TRAP"/>
	      <cvParam cvRef="MS" accession="MS:1000042" name="peak intensity" value="4072" unitCvRef="MS" unitAccession="MS:1000905" unitName="percent of base peak times 100"/>
	      <cvParam cvRef="MS" accession="MS:1000906" name="peak intensity rank" value="2"/>
	      <cvParam cvRef="MS" accession="MS:1000907" name="peak targeting suitability rank" value="1"/>
            </ValidationStatus>
          </Configuration>
        </ConfigurationList>
      </Product>
      <RetentionTime softwareRef="Skyline0.5">
        <cvParam cvRef="MS" accession="MS:1000895" name="local retention time" value="40.02" unitCvRef="UO" unitAccession="UO:0000031" unitName="minute"/>
        <cvParam cvRef="MS" accession="MS:1000916" name="retention time window lower offset" value="3.0" unitCvRef="UO" unitAccession="UO:0000031" unitName="minute"/>
        <cvParam cvRef="MS" accession="MS:1000917" name="retention time window upper offset" value="3.0" unitCvRef="UO" unitAccession="UO:0000031" unitName="minute"/>
      </RetentionTime>
      <Prediction softwareRef="MaRiMba" contactRef="CS">
	<cvParam cvRef="MS" accession="MS:1000912" name="transition purported from an MS/MS spectrum on a different, specified instrument"/>
	<cvParam cvRef="MS" accession="MS:1000291" name="linear ion trap"/>
	<cvParam cvRef="MS" accession="MS:1000042" name="peak intensity" value="10000" unitCvRef="MS" unitAccession="MS:1000905" unitName="percent of base peak times 100"/>
	<cvParam cvRef="MS" accession="MS:1000906" name="peak intensity rank" value="1"/>
	<cvParam cvRef="MS" accession="MS:1000907" name="peak targeting suitability rank" value="1"/>
      </Prediction>
    </Transition>

    <Transition id="ADTHFLLNIYDQLR-M1-T2" peptideRef="ADTHFLLNIYDQLR-M1">
      <Precursor>
        <cvParam cvRef="MS" accession="MS:1000827" name="isolation window target m/z" value="862.9467" unitCvRef="MS" unitAccession="MS:1000040" unitName="m/z"/>
        <cvParam cvRef="MS" accession="MS:1000828" name="isolation window lower offset" value="1.0" unitCvRef="MS" unitAccession="MS:1000040" unitName="m/z"/>
        <cvParam cvRef="MS" accession="MS:1000829" name="isolation window upper offset" value="1.0" unitCvRef="MS" unitAccession="MS:1000040" unitName="m/z"/>
        <cvParam cvRef="MS" accession="MS:1000041" name="charge state" value="2"/>
      </Precursor>
      <IntermediateProduct>
        <cvParam cvRef="MS" accession="MS:1000827" name="isolation window target m/z" value="1040.57" unitCvRef="MS" unitAccession="MS:1000040" unitName="m/z"/>
        <cvParam cvRef="MS" accession="MS:1000041" name="charge state" value="1"/>
        <InterpretationList>
          <Interpretation>
	    <cvParam cvRef="MS" accession="MS:1000926" name="product interpretation rank" value="1"/>
	    <cvParam cvRef="MS" accession="MS:1001220" name="frag: y ion"/>
	    <cvParam cvRef="MS" accession="MS:1000903" name="product ion series ordinal" value="8"/>
	    <cvParam cvRef="MS" accession="MS:1000904" name="product ion m/z delta" value="0.03" unitCvRef="MS" unitAccession="MS:1000040" unitName="m/z"/>
          </Interpretation>
        </InterpretationList>
        <ConfigurationList>
          <Configuration instrumentRef="QTRAP" contactRef="CS">
            <cvParam cvRef="MS" accession="MS:1000045" name="collision energy" value="26" unitCvRef="UO" unitAccession="UO:0000266" unitName="electronvolt"/>
          </Configuration>
        </ConfigurationList>
      </IntermediateProduct>
      <Product>
        <cvParam cvRef="MS" accession="MS:1000827" name="isolation window target m/z" value="543.2" unitCvRef="MS" unitAccession="MS:1000040" unitName="m/z"/>
        <cvParam cvRef="MS" accession="MS:1000041" name="charge state" value="1"/>
        <InterpretationList>
          <Interpretation>
	    <cvParam cvRef="MS" accession="MS:1000926" name="product interpretation rank" value="1"/>
	    <cvParam cvRef="MS" accession="MS:1001220" name="frag: y ion"/>
	    <cvParam cvRef="MS" accession="MS:1000903" name="product ion series ordinal" value="4"/>
	    <cvParam cvRef="MS" accession="MS:1000904" name="product ion m/z delta" value="0.03" unitCvRef="MS" unitAccession="MS:1000040" unitName="m/z"/>
          </Interpretation>
        </InterpretationList>
        <ConfigurationList>
          <Configuration instrumentRef="QTRAP" contactRef="CS">
            <cvParam cvRef="MS" accession="MS:1000502" name="dwell time" value="0.12" unitCvRef="UO" unitAccession="UO:0000010" unitName="second"/>
            <cvParam cvRef="MS" accession="MS:1000045" name="collision energy" value="20.4" unitCvRef="UO" unitAccession="UO:0000266" unitName="electronvolt"/>
          </Configuration>
        </ConfigurationList>
      </Product>
      <RetentionTime softwareRef="Skyline0.5">
        <cvParam cvRef="MS" accession="MS:1000895" name="local retention time" value="40.02" unitCvRef="UO" unitAccession="UO:0000031" unitName="minute"/>
        <cvParam cvRef="MS" accession="MS:1000916" name="retention time window lower offset" value="3.0" unitCvRef="UO" unitAccession="UO:0000031" unitName="minute"/>
        <cvParam cvRef="MS" accession="MS:1000917" name="retention time window upper offset" value="3.0" unitCvRef="UO" unitAccession="UO:0000031" unitName="minute"/>
      </RetentionTime>
    </Transition>

  </TransitionList>

  <TargetList>
    <cvParam cvRef="MS" accession="MS:1000920" name="includes supersede excludes"/>
    <TargetIncludeList>
      <Target id="PEPTIDEC2+" peptideRef="PEPTIDEC">
        <Precursor>
          <cvParam cvRef="MS" accession="MS:1000827" name="isolation window target m/z" value="862.9467" unitCvRef="MS" unitAccession="MS:1000040" unitName="m/z"/>
          <cvParam cvRef="MS" accession="MS:1000041" name="charge state" value="2"/>
        </Precursor>
        <RetentionTime softwareRef="Skyline0.5">
          <cvParam cvRef="MS" accession="MS:1000895" name="local retention time" value="27.44" unitCvRef="UO" unitAccession="UO:0000031" unitName="minute"/>
          <cvParam cvRef="MS" accession="MS:1000916" name="retention time window lower offset" value="4.0" unitCvRef="UO" unitAccession="UO:0000031" unitName="minute"/>
          <cvParam cvRef="MS" accession="MS:1000917" name="retention time window upper offset" value="4.0" unitCvRef="UO" unitAccession="UO:0000031" unitName="minute"/>
        </RetentionTime>
        <ConfigurationList>
          <Configuration instrumentRef="LCQ_Deca" contactRef="CS">
            <cvParam cvRef="MS" accession="MS:1000045" name="collision energy" value="26" unitCvRef="UO" unitAccession="UO:0000266" unitName="electronvolt"/>
          </Configuration>
        </ConfigurationList>
      </Target>
    </TargetIncludeList>
    <TargetExcludeList>
      <Target id="PEPTIDEM3+" peptideRef="PEPTIDEM">
        <Precursor>
          <cvParam cvRef="MS" accession="MS:1000827" name="isolation window target m/z" value="698.3443" unitCvRef="MS" unitAccession="MS:1000040" unitName="m/z"/>
          <cvParam cvRef="MS" accession="MS:1000041" name="charge state" value="3"/>
        </Precursor>
      </Target>
    </TargetExcludeList>
  </TargetList>
</TraML>
"""
		
	/*
	@Test
	def readFile = {
		var f = TraML.fromFile(new XmlReader(new StringReader(TRA_ML_FILE)))
		assertEquals(3, f.cvs.length)
		assertEquals(1, f.sourceFiles.length)
		assertEquals(1, f.contacts.length)
		assertEquals(1, f.publications.length)
		assertEquals(2, f.instruments.length)
		assertEquals(3, f.softwares.length)
		assertEquals(2, f.proteins.length)
		assertEquals(2, f.transitions.length)
		
		var cpl = f.compoundList
		assertTrue(cpl != null)
		assertEquals(3, cpl.peptides.length)
		assertEquals(1, cpl.compounds.length)
		
		var tl = f.targetList
		assertTrue(tl.isDefined)
		assertEquals(1, tl.get.targetIncludes.length)
		assertEquals(1, tl.get.targetExcludes.length)
		
		
		
		
		assert(f.peptides.contains("AAQVAQDEEIAR.2"))
		assert(f.peptides.contains("AEFSAGAWSEPR.2"))
		var p = f.peptides("AAQVAQDEEIAR.2")
		assertEquals("AAQVAQDEEIAR", p.sequence)
		assertEquals(18.61, p.start, 0.0001)
		assertEquals(28.61, p.end, 0.0001)
		
		assertEquals(4, f.transitions.length)
		var t = f.transitions(2)
		assertEquals(25.6, t.ce, 0.1)
		assertEquals(654.3049, t.q1, 0.0001)
		assertEquals(802.3838, t.q3, 0.0001)
	}
	*/
	
	@Test
	def readToyExample = {
		var f = TraML.fromFile(new XmlReader(new StringReader(TOY_EXAMPLE_TRAML)))
		assertEquals(3, f.cvs.length)
		assertEquals(1, f.sourceFiles.length)
		assertEquals(1, f.contacts.length)
		assertEquals(1, f.publications.length)
		assertEquals(2, f.instruments.length)
		assertEquals(3, f.softwares.length)
		assertEquals(2, f.proteins.length)
		assertEquals(2, f.transitions.length)
		
		var cpl = f.compoundList
		assertTrue(cpl != null)
		assertEquals(3, cpl.peptides.length)
		assertEquals(1, cpl.compounds.length)
		
		var tl = f.targetList
		assertTrue(tl.isDefined)
		assertEquals(1, tl.get.targetIncludes.length)
		assertEquals(1, tl.get.targetExcludes.length)
		
		
		assert(cpl.peptides.exists(_.id == "ADTHFLLNIYDQLR-M1"))
		assert(cpl.peptides.exists(_.id == "PEPTIDEC"))
		assert(cpl.peptides.exists(_.id == "PEPTIDEM"))
		var p = cpl.peptides.find(_.id == "ADTHFLLNIYDQLR-M1").get
		assertEquals("ADTHFLLNIYDQLR", p.sequence)
		assertEquals(2, p.proteinRefs.length)
		assertTrue(p.proteinRefs.contains("Q12149"))
		
		assertEquals(1, p.retentionTimes.length)
		
		var t = f.transitions(0)
		
		var q1 = t.precursor.cvParams.find(_.accession == "MS:1000827")
		assertTrue(q1.isDefined)
		assertEquals(862.9467, q1.get.value.get.toDouble, 0.0001)
		
		var q3 = t.product.cvParams.find(_.accession == "MS:1000827")
		assertTrue(q3.isDefined)
		assertEquals(1040.57, q3.get.value.get.toDouble, 0.0001)
		
		var ionType = t.product.interpretations(0).cvParams.find(_.accession == "MS:1001220")
		assertTrue(ionType.isDefined)
		
		var ionOrdinal = t.product.interpretations(0).cvParams.find(_.accession == "MS:1000903")
		assertTrue(ionOrdinal.isDefined)
		assertEquals(8, ionOrdinal.get.value.get.toInt)
		
		var ce = t.product.configurations(0).cvParams.find(_.accession == "MS:1000045")
		assertTrue(ce.isDefined)
		assertEquals(26, ce.get.value.get.toDouble, 0.1)
	}
}