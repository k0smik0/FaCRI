package net.iubris.facri.console.actions.graph.grapher;

import java.io.Console;
import java.io.IOException;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import net.iubris.facri.console.actions.graph.utils.UseCache;
import net.iubris.facri.console.actions.graph.utils.UseCache.UseCacheArguments;
import net.iubris.facri.model.graph.GraphsHolder;
import net.iubris.facri.parsers.DataParser;
import net.iubris.heimdall.actions.CommandAction;
import net.iubris.heimdall.actions.HelpAction;
import net.iubris.heimdall.command.ConsoleCommand;

public class GrapherAction implements CommandAction {
	
//	private final static String WRONG_ARGUMENTS_NUMBER = "analyzer needs two arguments; type 'h' for help\n";
//	private final static String WRONG_ARGUMENT = "wrong arguments for analysis: type 'h' for help\n";
	
	private final DataParser dataParser;
	private final GraphsHolder graphHolder;
	private final GrapherExecutor grapher;

	@Inject
	public GrapherAction(
			DataParser dataParser,
			// missing friendships generator
			GraphsHolder graphHolder,
			GrapherExecutor grapher
			) {
		this.dataParser = dataParser;
		this.graphHolder = graphHolder;
		this.grapher = grapher;
	}

	@Override
	public void exec(Console console, String... params) throws JAXBException, XMLStreamException, IOException {
//		try {
			if (params==null || (params.length<2)) {
				handleError(console, WRONG_ARGUMENTS_NUMBER);
				return;
			}
			
//			UseCache useCache = null;
//			if (params.length==3)
//				useCache = UseCache.handleUseCache(params[2], dataParser);
//			else {
//				useCache = UseCache.noCache();
//				dataParser.parse();
//			}
			
			UseCache useCache = UseCache.handleUseCache(params, dataParser);
			
			try {
				String worldTargetParam = params[0];
				GrapherExecutor.GraphType worldTarget = Enum.valueOf(GrapherExecutor.GraphTypeCommand.class, worldTargetParam).getWorldTarget();
				
//				Graph graph = 
						worldTarget.prepareGraph(graphHolder, useCache);

				String graphTypeParam = params[1];
				GrapherExecutor.WorldType graphType = Enum.valueOf(GrapherExecutor.WorldTypeCommand.class, graphTypeParam).getAnalysisType();
//				graphType.setDependencies(grapher.getGraphgenerator(worldTarget), useCache, worldTarget.name());
						
				grapher.execute( worldTarget, graphType, useCache, worldTarget.name() );
//				graphType.makeGraph();
				
			
			} catch(IllegalArgumentException e) {
				console.printf(WRONG_ARGUMENT);
			}
			
			/*
//			Graph graph = null;
			switch (worldTarget) {
			case friendships:
				// TODO use friendships generator
				break;
			case interactions:
//				graphHolder.prepareForDisplayInteractions();
//				graph = graphHolder.getInteractionsGraph();
//				if (useCache.read) {
//					useCache.read(INTERACTIONS_CACHE_FILENAME, graph);
//				}
				handleAnalysis(analysisType);
//				if (useCache.write) {
//					useCache.write(INTERACTIONS_CACHE_FILENAME, graph);					
//				}
				
				
				LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
				ViewerPipe viewerPipeInteractions = graphHolder.getViewerPipeInteractions(queue);
				viewerPipeInteractions.pump();
//				ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();				
//				Runnable runnable = new Runnable() {
//					@Override
//					public void run() {
//						while(true) {
//							try {
//								String polled = queue.poll(50, TimeUnit.MILLISECONDS);
//								System.out.println(polled);
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//							viewerPipeInteractions.pump();
//						}
//					}
//				};
//				newSingleThreadExecutor.execute(runnable);
				
				
				break;
			default:
				console.printf(WORLD_TARGET_WRONG_ARGUMENT);
				break;
			}
			*/
			
//		} catch (IllegalArgumentException e) {
//			handleError(console);
//		}
	}
	
	/*private UseCache handleUseCache(String[] params) throws JAXBException, XMLStreamException, IOException {
		boolean cacheRead = false, cacheWrite = false;
		// bad, but working
		if (params.length==3) {
			String useCacheString = params[2];
			UseCacheArguments useCacheArguments = UseCacheArguments.valueOf(useCacheString);
			try {
				switch(useCacheArguments) {
					// if read from cache, do not parse
					case cr:
						cacheRead = true;
						break;
					// write a cache, so parse for new data
					case cw:
						dataParser.parse();
						cacheWrite = true;
						break;
					default:
						break;
				}
			} catch (IllegalArgumentException e) {
				System.out.println("wrong argument");
			}

		} else {
			// no cache choise at all, so parse
			dataParser.parse();
		}
		return new UseCache(cacheRead, cacheWrite);
	}*/
	
	public enum GrapherCommand implements ConsoleCommand {
		G;
		@Override
		public String getHelpMessage() {
			return message;
		}
		private String message =  
			"graph [world] [actors] [use_cache]\n"
			+HelpAction.tab(2)+"'graph' command needs two arguments:\n"
			+HelpAction.tab(2)+"first argument select 'world' to graph:\n"
			+GrapherExecutor.GraphTypeCommand.f.getHelpMessage()
			+GrapherExecutor.GraphTypeCommand.i.getHelpMessage()
			+HelpAction.tab(2)+"second argument select 'actors' type:\n"
			+GrapherExecutor.WorldTypeCommand.mf.getHelpMessage()
			+GrapherExecutor.WorldTypeCommand.f.getHelpMessage()
			+GrapherExecutor.WorldTypeCommand.ft.getHelpMessage()
			+GrapherExecutor.WorldTypeCommand.t.getHelpMessage()
			+GrapherExecutor.WorldTypeCommand.mft.getHelpMessage()
			+HelpAction.tab(2)+"third argument uses cache for reading or writing generated graph:"+"\n"
			+UseCacheArguments.cr.getHelpMessage()
			+UseCacheArguments.cw.getHelpMessage()
			+HelpAction.tab(2)+"example: 'g i mf' -> graphs interactions between me and my friends";
	}
	
	/*public enum UseCacheArguments implements ConsoleCommand {
		cw("cache read: (try to) import previous generated graph from a cached file") {},
		cr("cache write: export generated graph on a cache file") {};
		UseCacheArguments(String helpMessageCore) {
			this.helpMessage = 
//					ConsoleCommand.Utils.
					getPrefix(this,3)+helpMessageCore+"\n";
		}
		@Override
		public String getHelpMessage() {
			return helpMessage;
		}
		private final String helpMessage;
	}*/
}
