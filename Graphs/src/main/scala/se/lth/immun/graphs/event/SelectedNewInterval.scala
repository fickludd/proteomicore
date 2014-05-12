package se.lth.immun.graphs.event

import swing.Component
import swing.event.SelectionChanged
import swing.event.Key

class SelectedNewInterval[T](
		var start:T,
		var end:T,
		var s:Component,
		var mods:Key.Modifiers = 1024
) extends SelectionChanged(s) {}