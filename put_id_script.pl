print "Hello world!\n";

open(INPUT, '<', 'A:\workspace\InMap\stores.xml') or die "Error opening $file_path - $!\n";
open(OUTPUT, '>A:\workspace\InMap\output.xml') or die "Error opening $file_path - $!\n";

$id = 1;
while($line = <INPUT>) { 
	if(index($line, "<store>", 0) != -1){
		$line = $line . "        <id>$id</id>\n";
		$id++;
	}
    print OUTPUT $line; 
  }

close(INPUT);
close(OUTPUT);

print "\nDone.";