package net.iubris.facri.parsers.friends;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import net.iubris.facri.parsers.Parser;
import net.iubris.facri.parsers.posts.PostsParser;
import net.iubris.facri.parsers.utils.ParsingUtils;
//import net.iubris.ishtaran.gui._di.annotations.ProgressBarGlobalSize;
import net.iubris.facri.utils.Timing;

public class FriendsDataParser implements Parser {
	
	private final String feedsFriendsDataDir;
	
	private final PostsParser postsParser;
	private final MutualFriendsParser mutualFriendsParser;
	
//	@ProgressBarGlobalSize
	private int usersTotal;
	
	private int userCounter;

	private int percentCounter;
	
	@Inject
	public FriendsDataParser(PostsParser postsParser, MutualFriendsParser mutualFriendsParser,
			@Named("data_root_dir_path") String dataRootDirPath, // "output"
			@Named("feeds_friends_dir_relative_path") String feedsFriendsDirRelativePath // "friends"
			) {
		this.postsParser = postsParser;
		this.mutualFriendsParser = mutualFriendsParser;
		this.feedsFriendsDataDir = dataRootDirPath+File.separatorChar+feedsFriendsDirRelativePath;
	}
	
	@Override
	public void parse(File... userDirs) {
		List<File> friendsDirectories = ParsingUtils.getDirectories(feedsFriendsDataDir);		
		setUsersTotal( friendsDirectories.size() );
System.out.print("Parsing posts on my friends walls: ");	
		parseUsersDirs( friendsDirectories );		
	}
	
	private void parseUsersDirs(List<File> usersDirs) {
		Timing timing = new Timing();

		// explicit stream
//		usersDirs.stream()
//		.parallel()
////		.sequential()
//		.forEach( new Consumer<File>() {
//			@Override
//			public void accept(File userDir) {				
//				String owningWallUserId = userDir.getName();
//				if (checkDir(userDir)) {
//					postsParser.parse(userDir, owningWallUserId, useridToUserMap);
//					mutualFriendsParser.parse(userDir, owningWallUserId, useridToUserMap);
////					System.out.println("ok");
//				} 
//			}
//		}); // end stream
		
		// directory stream
//		Path path = FileSystems.getDefault().getPath( feedsFriendsDataDir);
//		try {
//			DirectoryStream<Path> directoryStream = Files.newDirectoryStream( path );
//			directoryStream.forEach( new Consumer<Path>() {
//				@Override
//				public void accept(Path userDirPath) {
//					File userDir = userDirPath.toFile();
//					String owningWallUserId = userDir.getName();
//					if (checkDir(userDir)) {
//						postsParser.parse(userDir, owningWallUserId, useridToUserMap);
//						mutualFriendsParser.parse(userDir, owningWallUserId, useridToUserMap);
////						System.out.println("ok");
//					} 
//				}
//			});
//			directoryStream.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		// executor
//		Executor parseExecutor = Executors.newFixedThreadPool(1);
//		for (File userDir : usersDirs) {
//			Runnable parseRunnable = new Runnable() {			
//				@Override
//				public void run() {
//					String owningWallUserId = userDir.getName();
//					if (checkDir(userDir)) {
//						postsParser.parse(userDir, owningWallUserId, useridToUserMap);
//						mutualFriendsParser.parse(userDir, owningWallUserId, useridToUserMap);
////						System.out.println("ok");
//					} else
//						System.out.println("empty");
//				}
//			};
//			parseExecutor.execute(parseRunnable);
//		}
		
		// lambda stream
		userCounter = 0;
		usersDirs
		.stream()
		.parallel()
//		.sequential()
		.peek( 
			s->printPercentual( incrementUserCounter() )
		)
		.filter( s->s.listFiles().length>0 ) // check existant dir
		.parallel() // parallel on each directory
		.forEach( 
			ud->consumeUserDir(ud)
		);
		
		
		double finish = timing.getTiming();
System.out.println( "parsed "+usersTotal+" users in: "+finish+"s" );
		userCounter = 0;
		percentCounter = 0;
	}
	
	private void consumeUserDir(File userDir) {
//		String owningWallUserId = userDir.getName();
		postsParser.parse(userDir);
		mutualFriendsParser.parse(userDir);
//System.out.println("ok");
//		System.out.println("");
	}
	
	private void printPercentual(int userCounter) {
		double percent = Math.ceil( userCounter*1.0f/usersTotal*100 );
		if (Math.floor(percent)%10 == 0) {
			percentCounter++;
			if (percentCounter==1) {
				int toPrint = (int)percent;
				System.out.print(toPrint);
				if (toPrint<100) {
					System.out.print("%... ");					
				} else
					System.out.println("%");
			}
			if (percentCounter>9)
				percentCounter=0;
		}
	}
	
	private void setUsersTotal(int usersTotal) {
		this.usersTotal = usersTotal;	
	}
	private int incrementUserCounter() {
		return ++userCounter;
	}
}
