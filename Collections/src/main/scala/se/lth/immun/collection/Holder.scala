package se.lth.immun.collection

trait Holder[+T, SELF <: Holder[T, SELF]] extends AnyGiver {self:SELF => 
	def obj:T
	def any = obj
}
