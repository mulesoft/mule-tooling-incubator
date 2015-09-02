package org.mule.tooling.devkit.apt;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic.Kind;

import org.mule.devkit.generation.api.Context;
import org.mule.devkit.generation.api.gatherer.Notification;
import org.mule.devkit.generation.api.gatherer.NotificationGatherer;
import org.mule.devkit.generation.api.gatherer.printing.PrinterGatherer;

public class StudioPrinterGatherer implements PrinterGatherer {

	@Override
	public void printErrors(NotificationGatherer notificationGatherer,
			ProcessingEnvironment processingEnv, Context context) {
		for(Notification notification : notificationGatherer.getErrors()){
			processingEnv.getMessager().printMessage(Kind.ERROR, notification.getDetails().getMessage(), notification.getDetails().getElement().unwrap());
		}

	}

	@Override
	public void printWarnings(NotificationGatherer notificationGatherer,
			ProcessingEnvironment processingEnv, Context context) {
		for(Notification notification : notificationGatherer.getWarnings()){
			processingEnv.getMessager().printMessage(Kind.WARNING, notification.getDetails().getMessage(), notification.getDetails().getElement().unwrap());
		}

	}

	@Override
	public void printNotes(NotificationGatherer notificationGatherer,
			ProcessingEnvironment processingEnv, Context context) {
		//Ignore notes, since they add no value and leave the messages log full of garbage
	}

}
