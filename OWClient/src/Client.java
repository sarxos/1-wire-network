import java.awt.BorderLayout;
import java.awt.Dimension;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.sarxos.ow.MessageBroadcastListener;
import com.sarxos.ow.client.ApplicationContext;
import com.sarxos.ow.client.FrameToolkit;
import com.sarxos.ow.client.OWClient;
import com.sarxos.ow.client.graph.OWGraph;
import com.sarxos.ow.client.wrappers.AbstractDeviceWrapper;
import com.sarxos.ow.client.wrappers.Thermometer;
import com.sarxos.ow.device.DevicesArrivalListener;
import com.sarxos.ow.device.DevicesDepartureListener;
import com.sarxos.ow.device.event.DevicesArrivalEvent;
import com.sarxos.ow.device.event.DevicesDepartureEvent;
import com.sarxos.ow.device.event.MessageBroadcastEvent;
import com.sarxos.ow.device.rmi.RemoteDevice;
import com.sarxos.ow.device.rmi.RemoteTemperatureDevice;
import com.sarxos.ow.rmi.RemoteOWUserImpl;


public class Client extends JFrame implements DevicesArrivalListener, 
	DevicesDepartureListener, MessageBroadcastListener {

	private OWClient client = null;
	
	private List<RemoteDevice> devices = null;
	private Map<RemoteDevice, AbstractDeviceWrapper> localMapping = new HashMap<RemoteDevice, AbstractDeviceWrapper>();
	private OWGraph graph = null;
	
	public Client() {
		client = OWClient.getClient();
		RemoteOWUserImpl usr = client.getRemoteUser();
		usr.addDevicesArrivalListener(this);
		usr.addDevicesDepartureListener(this);
		usr.addMessageBroadcastListener(this);
		
		List <RemoteDevice> tmp = client.getDevices();
		devices = new ArrayList<RemoteDevice>();
		devices.addAll(tmp);

		for (RemoteDevice d : tmp) {
			localMapping.put(d, createDevice(d));
		}

		creteGUI();

		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						String laf = UIManager.getSystemLookAndFeelClassName();
						try {
							UIManager.setLookAndFeel(laf);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
		);
	}
	
	protected void creteGUI() {
		
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(700, 800));
		setTitle("Simple OW Devices Manager");
		
		graph = new OWGraph();
		ApplicationContext.getInstance().setCurrentGraph(graph);
		add(new JScrollPane(graph), BorderLayout.CENTER);

		if (devices != null && devices.size() > 0) {
			assert localMapping != null;
			assert devices.size() == localMapping.size(); 
			addDevicesToGraph();
		}
		
		pack();
		setVisible(true);
		
		FrameToolkit.centerWindow(this);
	}
	
	protected void addDevicesToGraph() {
		assert localMapping != null;
		for (AbstractDeviceWrapper wrapper : localMapping.values()) {
			graph.insertDevice(wrapper);
		}
	}
	
	protected AbstractDeviceWrapper createDevice(RemoteDevice d) {
		try {
			if (d instanceof RemoteTemperatureDevice) {
				return new Thermometer(d);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Brak klasy dla urzadzenia!");
	}
	
	public void devicesArival(DevicesArrivalEvent e) {
		int n = devices.size();
		List<RemoteDevice> arrivals = e.getDevices();
		devices.addAll(arrivals);
		for (RemoteDevice d : arrivals) {
			AbstractDeviceWrapper w = createDevice(d); 
			localMapping.put(d, w);
			graph.insertDevice(w);
		}
		
		System.out.println(
				"Devices previous vs. after [" + n + ":" + devices.size() + "]. Map [" +
				localMapping.size() + "]"
		);
	}

	public void devicesDeparture(DevicesDepartureEvent e) {
		int n = devices.size();
		List<RemoteDevice> departures = e.getDevices();
		for (Object d : departures) {
			localMapping.remove(d);
			// TODO: remove device from graph
		}
		devices.removeAll(departures);
		
		System.out.println(
				"Devices previous vs. after [" + n + ":" + devices.size() + "]. Map [" +
				localMapping.size() + "]"
		);
	}
 
	public void messageReceived(MessageBroadcastEvent e) {
		JOptionPane.showMessageDialog(this,e.getMessage());
	}

	public static void main(String[] args) {
		System.setSecurityManager(new RMISecurityManager());
		new Client();
	}
}
