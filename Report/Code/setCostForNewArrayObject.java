private void setCostForNewArrayObject(CostResultMemory cost,
	TypeName typeName, 
	String typeNameStr, 
	ISSABasicBlock block) {

		Integer arrayLength = tryGetArrayLength(block);
		
		IBytecodeMethod method = (IBytecodeMethod)block.getMethod();
		int lineNumber = method.getLineNumber(block.getFirstInstructionIndex());
		
		if(arrayLength == null) {
			Map<Integer, Annotation> annotationsForMethod;
			annotationsForMethod = extractor.getAnnotations(method);
			
			if (annotationsForMethod.containsKey(lineNumber)) {
				Annotation annotationForArray = annotationsForMethod.get(lineNumber);
				arrayLength = Integer.parseInt(annotationForArray.getAnnotationValue());
			}
			else {
				// Warning: allocates array without specified memory size annotation
			}
		}
		
		try {
			int allocationCost = arrayLength * model.getSizeForQualifiedType(typeName);
			cost.allocationCost = allocationCost;
			cost.typeNameByNodeId.put(block.getGraphNodeId(), typeName);
		}
		catch(NoSuchElementException e) {
			// Warning: model does not contain array type
		}
	}