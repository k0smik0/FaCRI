package net.iubris.facri.model.graph;

import java.awt.Desktop;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.inject.Singleton;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;
import org.graphstream.ui.swingViewer.util.Camera;
import org.graphstream.ui.swingViewer.util.DefaultMouseManager;
import org.graphstream.ui.swingViewer.util.DefaultShortcutManager;

@Singleton
public class GraphHolder {
	
	private final Graph friendshipsGraph;
	private final Graph interactionsGraph;
	private final Viewer friendshipsGraphViewer;
	private final Viewer interactionsGraphViewer;
//	private ViewerPipe fromViewerInteractions;
	
	
	/*private class NodeViewerListener implements ViewerListener {
		private final Graph graph;
		private LinkedBlockingQueue<String> queue;
//		private boolean loop = true;
		
		public NodeViewerListener(Graph graph, LinkedBlockingQueue<String> queue) {
			this.graph = graph;
			this.queue = queue;
		}
		@Override
		public void viewClosed(String viewName) {
//			loop = false;
		}
		@Override
		public void buttonReleased(String id) {}
		@Override
		public void buttonPushed(String id) {
			System.out.println(id+": "+graph.getNode(id).getDegree());
			queue.add(id);
		}
//		public void doLoop() {
//			loop = true;
//		}
	};*/
	
	public GraphHolder() {
		this.friendshipsGraph = new MultiGraph("Friendships",false,true);
		this.friendshipsGraphViewer = prepareForDisplay(friendshipsGraph);
		this.interactionsGraph = new MultiGraph("Interactions",false,true);
//		System.out.println(interactionsGraph);
		this.interactionsGraphViewer = prepareForDisplay(interactionsGraph);
		
//		System.out.print("[");		
		System.setProperty("sun.java2d.opengl", "True");
//		System.out.println("]");
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		System.out.print("\n");
	}
	
	private Viewer prepareForDisplay(Graph graph) {
		graph.addAttribute("ui.quality");
		graph.addAttribute("ui.antialias");
//		graph.addAttribute("ui.speed");
		
//		graph.display();

//		System.out.println("graph: "+graph);
		Viewer viewer = new Viewer(graph,  Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
//		System.out.println("viewer: "+viewer);
		viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
//		viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
		viewer.enableAutoLayout();
		
		
		
		return viewer;
	}
	
	
	public void prepareForDisplayFriendships() {
		friendshipsGraphViewer.close();
//		friendshipsGraphViewer.getDefaultView().setVisible(true);
	}
	
	public void prepareForDisplayInteractions() {
		View view = interactionsGraphViewer.getDefaultView();
		if (view==null) {
//			System.out.println("defaultView "+defaultView);
//			defaultView.setVisible(false);
			view = interactionsGraphViewer.addDefaultView(true);
//			System.out.println("defaultView "+defaultView);
		}
		view.setVisible(true);
		
		view.setMouseManager(new InternalMouseManager(interactionsGraphViewer));
		view.setShortcutManager(new InternalShortcutManager(interactionsGraphViewer));
	}
	
	class InternalMouseManager extends DefaultMouseManager {
		private final Viewer viewer;
//		private double zoom;
//		private Camera camera;
		
//		boolean focusOnClick = true;
		
		public InternalMouseManager(Viewer viewer) {
			this.viewer = viewer;
//			camera = viewer.getDefaultView().getCamera();
//			zoom = viewer.getDefaultView().getCamera().getViewPercent();
		}
		
		
		
//		@Override
		protected void mouseButtonRelease(MouseEvent event, ArrayList<GraphicElement> elements) {
			super.mouseButtonRelease(event, elements);
//			System.out.println(  event.getComponent() );
//			
//			if (event.isControlDown())
//				elements.parallelStream()
//	//				.forEach(e->e.setAttribute("ui.class", "nottexted");System.out.println(););
//				.forEach( new Consumer<GraphicElement>() {
//					@Override
//					public void accept(GraphicElement e) {
//	//					e.getAttributeKeySet();
//						e.setAttribute("ui.class", "nottexted");
//						System.out.println(e.getAttribute("ui.class"));
//						
//					}
//				});
			elements.parallelStream()
							.forEach(e->e.removeAttribute("ui.label"));
		}
		
		@Override
		public void mouseEntered(MouseEvent event) {
			// TODO Auto-generated method stub
			super.mouseEntered(event);
//			camera.setViewPercent(zoom);
		}
		

		@Override
		protected void mouseButtonPressOnElement(GraphicElement element, MouseEvent event) {
			super.mouseButtonPressOnElement(element, event);
 
			if (event.getButton()==3 && element instanceof GraphicNode) {
				element.setAttribute("ui.label",element.getId());
			}
			if (event.getButton()==3 && event.isShiftDown() && element instanceof GraphicNode) {
				element.removeAttribute("ui.label");
			}
			
			if (event.getButton()==1 && event.isShiftDown() && /*focusOnClick &&*/ element instanceof GraphicNode) {
				double x = element.getX();
				double y = element.getY();
				double z = element.getZ();
 
				Camera camera = viewer.getDefaultView().getCamera();
				camera.setViewCenter(x, y, z);
				
				String profileUrl = "https://www.facebook.com/profile.php?id="+element.getId();
				String nodeId = element.getId();
				Node node = viewer.getGraphicGraph().getNode(nodeId);
				String nodeInfo = "in-degree: "+node.getInDegree()+"<br/>out-degree: "+node.getOutDegree()+"<br/>url: <a href='"+profileUrl+"'>"+profileUrl+"</a>";
				JEditorPane ep = new JEditorPane("text/html", nodeInfo);
				buildJEditorPane(ep);
				JOptionPane.showMessageDialog(view, 
//						"You click on " + element.getId()
						ep
						);
			}
		}
	}
	class InternalShortcutManager extends DefaultShortcutManager {
		public InternalShortcutManager(Viewer viewer) {
		}
		@Override
		public void keyTyped(KeyEvent event) {
			super.keyTyped(event);
			switch(event.getKeyChar()) {
			case 'c':
//				GraphicGraph graphicGraph = viewer.getGraphicGraph();
//				Iterator<Node> iterator = graphicGraph.iterator();
//				if (iterator.hasNext()) {
//					Node next = iterator.next();
//					viewer
//						.getDefaultView()
//						.getCamera().setViewCenter(next.get, y, z);
//				}
				break;
			default:
				break;
			}
		}
	}
	
	public void hideInteractionsGraph() {
		interactionsGraphViewer.getDefaultView().setVisible(false);
//		interactionsGraph.
	}

	public Graph getInteractionsGraph() {
		return interactionsGraph;
	}
	
	/*public ViewerPipe getViewerPipeInteractions(LinkedBlockingQueue<String> queue) {
		fromViewerInteractions = interactionsGraphViewer.newViewerPipe();
//		fromViewerInteractions.addViewerListener( new NodeViewerListener(interactionsGraph, queue) );
      fromViewerInteractions.addSink(interactionsGraph);
		return fromViewerInteractions;
	}*/
	
	public Graph getFriendshipsGraph() {
		return friendshipsGraph;
	}
	
	
	private static void launchUrl(String urlToLaunch) {
       try {
//           System.out.println("Launching url [" + urlToLaunch + "]");
           if ( !Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported( java.awt.Desktop.Action.BROWSE) ){
               JOptionPane.showMessageDialog(null, "On this computer java cannot open automatically url in browser, you have to copy/paste it manually.");
               return;
           }
           
           Desktop desktop = Desktop.getDesktop();
           URI uri = new URI(urlToLaunch);
           
           desktop.browse(uri);
       } catch (URISyntaxException ex) {
//           Logger.getLogger(GraphHolder.class.getName()).log(Level.SEVERE, null, ex);
           JOptionPane.showMessageDialog(null, "Url ["  + urlToLaunch + "] seems to be invalid ");
       } catch (IOException ex) {
//           Logger.getLogger(GraphHolder.class.getName()).log(Level.SEVERE, null, ex);
           JOptionPane.showMessageDialog(null, "There was some error opening the url. \n Details:\n" + ex.getMessage());
       }       
   }
	
	private void buildJEditorPane(JEditorPane ep) {
		ep.addHyperlinkListener(new HyperlinkListener() {
         @Override
         public void hyperlinkUpdate(HyperlinkEvent e) {
             if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                 if ( e.getURL() == null ){
//                     System.out.println("e.getURL: is NULL");
                     JOptionPane.showMessageDialog(null, "The text was clicked but hyperlink seems to contain invalid url and thus is NULL");
                 }
                 else{
//                     System.out.println("e.getURL: " + e.getURL());
                     launchUrl(e.getURL().toString()); // roll your own link launcher or use Desktop if J6+
                 }
             }
         }
     });
     ep.setEditable(false);
	}
	
}
