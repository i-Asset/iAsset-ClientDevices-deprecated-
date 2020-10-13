package org.srfg.support.directory;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;

public class ExampleAASRegistry extends InMemoryRegistry {

	public ExampleAASRegistry addAASMapping(String rawUrn, String endpoint) {
		IIdentifier id = new Identifier(IdentifierType.CUSTOM, rawUrn);
		AASDescriptor aasDescriptor = new AASDescriptor(id, endpoint);
		register(aasDescriptor);
		return this;
	}

	public ExampleAASRegistry addSubmodelMapping(String rawAASUrn, String submodelid, String endpoint) {
		AASDescriptor aasDescriptor;
		ModelUrn aasUrn = new ModelUrn(rawAASUrn);
		IIdentifier smId = new Identifier(IdentifierType.CUSTOM, submodelid);
		SubmodelDescriptor smDes = new SubmodelDescriptor(submodelid, smId, endpoint);

		if (descriptorMap.keySet().contains(aasUrn.getURN())) {
			aasDescriptor = descriptorMap.get(aasUrn.getURN());
		} else {
			throw new RuntimeException("AASDescriptor for " + rawAASUrn + " missing");
		}
		aasDescriptor.addSubmodelDescriptor(smDes);
		register(aasDescriptor);
		return this;
	}
}
