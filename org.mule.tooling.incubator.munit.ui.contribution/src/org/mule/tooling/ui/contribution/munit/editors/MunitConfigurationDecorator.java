package org.mule.tooling.ui.contribution.munit.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.mule.tooling.model.messageflow.Flow;
import org.mule.tooling.model.messageflow.MuleConfiguration;
import org.mule.tooling.model.messageflow.decorator.MuleConfigurationDecorator;
import org.mule.tooling.ui.contribution.munit.common.Filter;

/**
 * <p>
 * The {@link MuleConfigurationDecorator} extension for the Munit projects. This class has helper methods to work over Munit suites. 
 * </p>
 */
public class MunitConfigurationDecorator extends MuleConfigurationDecorator
{
	private static final String MUNIT_TEST_TYPE = "http://www.mulesoft.org/schema/mule/munit/test";

	/**
	 * <p>
	 * Accepts Munit tests.
	 * </p>
	 * @param flowName
	 * <p>The Name of the Munit test</p>
	 */
	public static Filter<Flow> munitTestFilter()
	{
		return new Filter<Flow>() {
			@Override
			public boolean accept(Flow flow) {
				return isMunitTest(flow);
			}
		};
	}

	/**
	 * <p>
	 * Accepts all flows
	 * </p>			
	 */
	public static Filter<Flow> anyFilter()
	{
		return new Filter<Flow>() {
			@Override
			public boolean accept(Flow flow) {
				return  true;
			}
		};
	}


	/**
	 * <p>
	 * Validates if the mule flow is an Munit test
	 * </p>
	 */
	public static boolean isMunitTest(Flow flow)
	{
		return flow.getType().equals(MUNIT_TEST_TYPE);
	}

		public MunitConfigurationDecorator(MuleConfiguration entity) {
		super(entity);
	}

	/**
	 * @return
	 * 		<p>
	 * 			All the flows from the Mule configuration that are Munit tests
	 * 		</p>
	 */
	public List<Flow> getMunitTests()
	{
		List<Flow> tests = new ArrayList<Flow>();
		List<Flow> flows = getFlows();
		for ( Flow flow : flows )
		{
			if ( isMunitTest(flow) ){
				tests.add(flow);
			}
		}
		return tests;
	}


	/**
	 * @param 
	 * 		Collection of {@link FlowFilter} that allows the method to select the required flows. If one filter accepts the flow then the it appears in the filtered {@link MuleConfiguration}
	 * @return
	 * 		The filtered {@link MuleConfiguration}
	 */
	public MuleConfigurationDecorator getMunitSuiteConfiguration(Collection<Filter<Flow>> filters)
	{
		MuleConfiguration muleConfiguration = new MuleConfiguration();
		muleConfiguration.setName(getEntity().getName());
		muleConfiguration.setEntityId(getEntity().getEntityId());

		for ( Flow flow : getFlows() )
		{
			for ( Filter<Flow> filter : filters )
			{
				if (filter.accept(flow)){
					muleConfiguration.getFlows().add(flow);
				}
			}
		}
		MuleConfigurationDecorator muleConfigurationDecorator = new MuleConfigurationDecorator(muleConfiguration);
		muleConfigurationDecorator.addObserver(new FlowMirrorObserver(this,  muleConfiguration.getFlows()));
		return muleConfigurationDecorator;
	}
	
	private static class FlowMirrorObserver implements Observer
	{
		MunitConfigurationDecorator mirrorDecorator;
		private List<Flow> originalFilteredFlows;

		public FlowMirrorObserver(MunitConfigurationDecorator mirrorDecorator, List<Flow> originalFilteredFlows) {
			this.mirrorDecorator = mirrorDecorator;
			this.originalFilteredFlows = originalFilteredFlows;
		}
		
		@Override
		public void update(Observable arg0, Object arg1) {
			// TODO: It would be better to have Flows as an observable list
			if ( arg1.equals(MuleConfigurationDecorator.PROP_FLOWS))
			{
				MuleConfigurationDecorator muleConfigurationDecorator = (MuleConfigurationDecorator) arg0;
				List<Flow> mirroredFlows = mirrorDecorator.getFlows();
				for ( Flow flow : muleConfigurationDecorator.getFlows() ){
					if ( !mirrorDecorator.getFlows().contains(flow) )
					{
						mirroredFlows.add(flow);
					}
				}
				for ( Flow flow : originalFilteredFlows ){
					if ( !muleConfigurationDecorator.getFlows().contains(flow) )
					{
						mirroredFlows.remove(flow);
					}
				}
			}
		}
		
	}
}
