package com.takipi.api.client.request.event;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;
import com.takipi.api.client.request.ViewTimeframeRequest;
import com.takipi.api.client.result.event.EventsResult;
import com.takipi.api.core.request.intf.ApiGetRequest;
import com.takipi.common.util.CollectionUtil;

public abstract class BaseEventsRequest extends ViewTimeframeRequest {
	public final boolean includeStacktrace;
	public final boolean breakServers;
	public final boolean breakApps;
	public final boolean breakDeployments;

	BaseEventsRequest(String serviceId, String viewId, String from, String to, boolean raw, Collection<String> servers,
			Collection<String> apps, Collection<String> deployments, boolean includeStacktrace, boolean breakServers,
			boolean breakApps, boolean breakDeployments) {
		super(serviceId, viewId, from, to, raw, servers, apps, deployments);

		this.includeStacktrace = includeStacktrace;
		this.breakServers = breakServers;
		this.breakApps = breakApps;
		this.breakDeployments = breakDeployments;
	}

	@Override
	protected int paramsCount() {
		// One slot for the stacktace flag and three for breakdown.
		//
		return super.paramsCount() + 4;
	}

	@Override
	protected int fillParams(String[] params, int startIndex) throws UnsupportedEncodingException {
		int index = super.fillParams(params, startIndex);

		params[index++] = "stacktrace=" + Boolean.toString(includeStacktrace);

		params[index++] = "breakServers=" + Boolean.toString(breakServers);
		params[index++] = "breakApps=" + Boolean.toString(breakApps);
		params[index++] = "breakDeployments=" + Boolean.toString(breakDeployments);

		return index;
	}

	public static abstract class Builder extends ViewTimeframeRequest.Builder {
		protected boolean includeStacktrace;
		protected boolean breakServers;
		protected boolean breakApps;
		protected boolean breakDeployments;

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		@Override
		public Builder setViewId(String viewId) {
			super.setViewId(viewId);

			return this;
		}

		@Override
		public Builder setRaw(boolean raw) {
			super.setRaw(raw);

			return this;
		}

		@Override
		public Builder setFrom(String from) {
			super.setFrom(from);

			return this;
		}

		@Override
		public Builder setTo(String to) {
			super.setTo(to);

			return this;
		}

		@Override
		public Builder addServer(String server) {
			super.addServer(server);

			return this;
		}

		@Override
		public Builder addApp(String app) {
			super.addApp(app);

			return this;
		}

		@Override
		public Builder addDeployment(String deployment) {
			super.addDeployment(deployment);

			return this;
		}

		public Builder setIncludeStacktrace(boolean includeStacktrace) {
			this.includeStacktrace = includeStacktrace;

			return this;
		}

		public Builder setBreakServers(boolean breakServers) {
			this.breakServers = breakServers;

			return this;
		}

		public Builder setBreakApps(boolean breakApps) {
			this.breakApps = breakApps;

			return this;
		}

		public Builder setBreakDeployments(boolean breakDeployments) {
			this.breakDeployments = breakDeployments;

			return this;
		}

		public Set<BreakdownType> getBreakFilters() {
			Set<BreakdownType> breakdownTypes = Sets.newHashSet();

			if (!CollectionUtil.safeIsEmpty(servers)) {
				breakdownTypes.add(BreakdownType.Server);
			}

			if (!CollectionUtil.safeIsEmpty(apps)) {
				breakdownTypes.add(BreakdownType.App);
			}

			if (!CollectionUtil.safeIsEmpty(deployments)) {
				breakdownTypes.add(BreakdownType.Deployment);
			}

			return breakdownTypes;
		}

		public abstract ApiGetRequest<EventsResult> build();
	}
}
