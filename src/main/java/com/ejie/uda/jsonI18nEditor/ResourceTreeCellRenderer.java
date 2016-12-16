package com.ejie.uda.jsonI18nEditor;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.apache.commons.lang3.StringUtils;

public class ResourceTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	private Icon emptyIcon;
	
	private Editor editor;
	
	
	
	public ResourceTreeCellRenderer(Editor editor, Icon emptyIcon){
		this.emptyIcon = emptyIcon;
		this.editor = editor;
	}
	
	public Component getTreeCellRendererComponent(
			JTree tree,
			Object value,
			boolean sel,
			boolean expanded,
			boolean leaf,
			int row,
			boolean hasFocus) {

		super.getTreeCellRendererComponent(
				tree, value, sel,
				expanded, leaf, row,
				hasFocus);
		
		String key = ((TranslationTreeNode)value).getKey();
		
		boolean hasEmpty = false;
		for (Resource resource : editor.getResources()) {
			if (StringUtils.isBlank(resource.getTranslation(key))){
				hasEmpty = true;
			}
		}
 		if (leaf && hasEmpty) {
			setIcon(this.emptyIcon);
			setToolTipText("This book is in the Tutorial series.");
		} else {
			setToolTipText(null); // no tool tip
		}

		return this;
	}
	
	protected boolean isTutorialBook(Object value) {
        return true;
    }
}