package miss.ui;

import javax.swing.JPanel;

import repast.simphony.relogo.factories.AbstractReLogoPanelCreator;

public class UserPanelCreator extends AbstractReLogoPanelCreator {

	@Override
	public void addComponents(JPanel parent) {
		UserGlobalsAndPanelFactory ugpf = new UserGlobalsAndPanelFactory();
		ugpf.initialize(parent);
		ugpf.addGlobalsAndPanelComponents();
	}

}
