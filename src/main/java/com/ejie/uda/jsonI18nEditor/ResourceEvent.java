package com.ejie.uda.jsonI18nEditor;

/**
 * An event wrapper for a {@link Resource}.
 * 
 * @author Jacob
 */
public class ResourceEvent {
	private final Resource resource;
	
	/**
	 * Creates an event object for a {@link Resource}.
	 * 
	 * @param 	resource the resource.
	 */
	public ResourceEvent(Resource resource) {
		this.resource = resource;
	}
	
	/**
	 * Gets the resource wrapped by this event object.
	 * 
	 * @return 	the resource.
	 */
	public Resource getResource() {
		return resource;
	}
}
