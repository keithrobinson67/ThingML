/*
 * generated by Xtext 2.10.0
 */
package org.thingml.xtext.ui.outline

import com.google.inject.Inject
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.ui.IImageHelper
import org.eclipse.xtext.ui.editor.outline.IOutlineNode
import org.eclipse.xtext.ui.editor.outline.impl.DefaultOutlineTreeProvider
import org.eclipse.xtext.ui.editor.outline.impl.DocumentRootNode
import org.thingml.xtext.thingML.CompositeState
import org.thingml.xtext.thingML.Function
import org.thingml.xtext.thingML.Message
import org.thingml.xtext.thingML.Port
import org.thingml.xtext.thingML.Property
import org.thingml.xtext.thingML.State
import org.thingml.xtext.thingML.StateContainer
import org.thingml.xtext.thingML.Thing
import org.thingml.xtext.thingML.ThingMLModel
import org.thingml.xtext.thingML.Session
import org.thingml.xtext.thingML.Region
import org.thingml.xtext.thingML.Transition
import org.thingml.xtext.thingML.Configuration

/**
 * Customization of the default outline structure.
 *
 * See https://www.eclipse.org/Xtext/documentation/304_ide_concepts.html#outline
 */
class ThingMLOutlineTreeProvider extends DefaultOutlineTreeProvider {
	@Inject
	private IImageHelper imageHelper;
	
	def _createChildren(DocumentRootNode outlineNode, ThingMLModel model) {
		model.types.forEach[type | createNode(outlineNode, type)];
		model.protocols.forEach[protocol | createNode(outlineNode, protocol)]
		model.configs.forEach[config | createNode(outlineNode, config)]
	}
	
	def _createChildren(IOutlineNode parent, Configuration cfg) {
		cfg.instances.forEach[createNode(parent, it)]
		cfg.connectors.forEach[createNode(parent, it)]
	}
	
	def _createChildren(IOutlineNode parent, Thing thing) {
		thing.messages.forEach[createNode(parent, it)]
		thing.ports.forEach[createNode(parent, it)]
		thing.properties.forEach[createNode(parent, it)]
		thing.functions.forEach[createNode(parent, it)]
		//thing.assign.forEach[createNode(outlineNode, it)]
		thing.behaviour.forEach[createNode(parent, it)]
	}
	
	def _isLeaf(Port port) { false }
	def _createChildren(IOutlineNode parent, Port port) {
		port.receives.forEach[customNode(parent, it, 'outline/inst_ptr.gif', it.name)]
		port.sends.forEach[customNode(parent, it, 'outline/inst_ptr_top_flipped.gif', it.name)]
	}
	
	def _createChildren(IOutlineNode parent, CompositeState composite) {
		state(parent, composite)
		session(parent, composite)
		container(parent, composite)
	}
	def _createChildren(IOutlineNode parent, Region region) {
		container(parent, region)
	}
	def _createChildren(IOutlineNode parent, Session session) {
		container(parent, session)
	}
	def _createChildren(IOutlineNode parent, State state) {
		state(parent, state)
	}
	
	def _isLeaf(Transition transition) { true }
	def _isLeaf(Message message) { true }
	def _isLeaf(Function function) { true }
	def _isLeaf(Property property) { true }
	
	/* --- Helpers --- */
	def container(IOutlineNode parent, StateContainer container) {
		container.substate.forEach[createNode(parent, it)]
	}
	
	def state(IOutlineNode parent, State state) {
		state.properties.forEach[createNode(parent, it)]
		//if (state.entry !== null) customNode(...)
		//if (state.exit !== null) customNode(...)
		state.outgoing.forEach[createNode(parent, it)]
		state.internal.forEach[createNode(parent, it)]
	}
	
	def session(IOutlineNode parent, CompositeState state) {
		state.session.forEach[createNode(parent, it)]
		state.region.forEach[createNode(parent, it)]
	}
	
	def customNode(IOutlineNode parent, EObject modelElement, String image, String text) {
		createEObjectNode(parent, modelElement, imageHelper.getImage(image), text, true)
	}
}
