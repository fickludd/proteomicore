package se.lth.immun.graphs.util

object Lambda {

	def trimPrefix(strs:Seq[String]):Seq[String] = {
		
		def sharedPrefix(a:String, b:String):Boolean = 
			if (a.length == 0 || b.length == 0)	false
			else 						a.head == b.head		
		
		
		strs.toList match {
			case str::Nil => return strs
			case str::rest => 	if (rest.forall(sharedPrefix(_, str))) 
									trimPrefix(strs.map(_.tail))
								else
									strs
			case _  => return Nil
		}
	}

	def trimSuffix(strs:Seq[String]):Seq[String] = trimPrefix(strs.map(_.reverse)).map(_.reverse)
	def trim(strs:Seq[String]):Seq[String] = trimPrefix(trimSuffix(strs))
	
	
	def iterate(d0:Double, dn:Double, action:Double => Unit):Unit =  iterate(d0, dn, 1.0, action)
	def iterate(d0:Double, dn:Double, dd:Double, action:Double => Unit):Unit = {
		var d = d0
		while (d < dn) {
			action(d)
			d += dd
		}
	}
	
	def iterate(i0:Int, in:Int, action:Int => Unit):Unit = iterate(i0, in, 1, action)
	def iterate(i0:Int, in:Int, di:Int, action:Int => Unit):Unit = {
		var i = i0
		while (i < in) {
			action(i)
			i += di
		}
	}
	
	def weave[A](s1:Seq[A], s2:Seq[A]):Seq[A] = {
		var b = s1.genericBuilder[A]
		s1.zip(s2).map(t => {b += t._1; b += t._2 })
		return b.result
	}
	
	def unweave[A](s:Seq[A]):Tuple2[Seq[A], Seq[A]] = {
		var b1 = s.genericBuilder[A]
		var b2 = s.genericBuilder[A]
		var i = s.iterator
		while (i.hasNext) {
			b1 += i.next
			b2 += i.next
		}
		return new Tuple2(b1.result, b2.result)
	}
}