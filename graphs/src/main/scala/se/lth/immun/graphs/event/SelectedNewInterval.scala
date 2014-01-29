package se.lth.immun.graphs.event

import swing.Component
import swing.event.SelectionChanged

class SelectedNewInterval[T](
		var start:T,
		var end:T,
		var s:Component
) extends SelectionChanged(s) {}