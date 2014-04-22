package se.lth.immun.chem;

import java.util.HashMap;
import java.util.Map;

public class ElementComposition {

	Element[] 			elements;
	int[] 				counts;
	IsotopeDistribution dist;

	public Element[] getElements() { return elements; }
	public int[] getCounts() { return counts; }

	public ElementComposition(Element[] elements, int[] counts) {
		this.elements = elements;
		this.counts = counts;
	}
	
	public double monoisotopicMass() {
		double x = 0.0;
		for (int i = 0; i < elements.length; i++)
			x += ((double)elements[i].monoisotopicWeight) * counts[i];
		return x;
	}
	
	public ElementComposition join(
			ElementComposition e
	) {
		HashMap<Element, Integer> hm = new HashMap<Element, Integer>();
		for (int i = 0; i < elements.length; i++)
			hm.put(elements[i], counts[i]);
		for (int i = 0; i < e.elements.length; i++)
			if (hm.containsKey(e.elements[i])) hm.put(e.elements[i], hm.get(e.elements[i]) + e.counts[i]);
			else hm.put(e.elements[i], e.counts[i]);
		Element[] eret 	= new Element[hm.size()];
		int[] cret 		= new int[eret.length];
		int i = 0;
		for (Map.Entry<Element, Integer> entry: hm.entrySet()) {
			eret[i] = entry.getKey();
			cret[i] = entry.getValue();
			i++;
		}
		return new ElementComposition(eret, cret);
	}
	
	public ElementComposition multiply(int times) {
		int[] cret = new int[counts.length];
		for (int i = 0; i < counts.length; i++) {
			cret[i] = counts[i] * times;
		}
		return new ElementComposition(elements, cret);
	}
	
	
	
	public static ElementComposition join(
			ElementComposition e1, 
			ElementComposition e2
	) {
		return e1.join(e2);
	}
	
	
	public IsotopeDistribution getIsotopeDistribution() {
		if (dist == null) {
			dist = new IsotopeDistribution();
			for (int i = 0; i < elements.length; i++)
				dist.add(elements[i].isotopeDistribution.mult(counts[i]));
		}
		return dist;
	}
}
