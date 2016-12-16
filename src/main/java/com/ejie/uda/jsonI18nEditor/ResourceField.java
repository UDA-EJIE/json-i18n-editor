package com.ejie.uda.jsonI18nEditor;

import java.awt.Color;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.undo.UndoManager;

/**
 * This class represents a text area to edit the value of a translation.
 * 
 * @author Jacob
 */
public class ResourceField extends JTextArea implements Comparable<ResourceField> {
	private final static long serialVersionUID = 2034814490878477055L;
	private final Resource resource;
	private final UndoManager undoManager = new UndoManager();
	
	public ResourceField(Resource resource) {
		super();
		this.resource = resource;
		setupUI();
	}
	
	public String getValue() {
		return getText().trim();
	}
	
	public void updateValue(String key) {
		setText(resource.getTranslation(key));
		undoManager.discardAllEdits();
	}
	
	public Resource getResource() {
		return resource;
	}
	
	private void setupUI() {
		Border border = BorderFactory.createLineBorder(Color.GRAY);
		setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(4,4,4,4)));
		setAlignmentX(LEFT_ALIGNMENT);
		setLineWrap(true);
		setWrapStyleWord(true);
		setRows(10);
		
		getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));
		
		// Add undo support
		getActionMap().put("undo", new UndoAction());
		getInputMap().put(KeyStroke.getKeyStroke('Z', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "undo");
		
		// Add redo support
		getActionMap().put("redo", new RedoAction());
		getInputMap().put(KeyStroke.getKeyStroke('Y', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "redo");
		
		// Add focus traversal support
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
	    setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
	}
	
	@SuppressWarnings("serial")
	private class UndoAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (undoManager.canUndo()) {
				undoManager.undo();
			}
		}
	}
	
	@SuppressWarnings("serial")
	private class RedoAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (undoManager.canRedo()) {
				undoManager.redo();
			}
		}
	}
	
	@Override
	public int compareTo(ResourceField o) {
		String a = getResource().getLocale().getDisplayName();
		String b = o.getResource().getLocale().getDisplayName();
		return a.compareTo(b);
	}
}
