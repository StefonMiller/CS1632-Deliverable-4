# CS 1632 - Software Quality Assurance
Fall Semester 2020

DUE: Nov 2 (Monday), 2020 09:00 AM

## Deliverable 4

For this assignment, your group will profile a Conway's Game of Life
simulation, and improve its performance by refactoring several methods (to be
determined by the results of the profiling).  This will consist of several
parts:

1. Profiling (before) to determine which method is the most CPU-intensive
2. Adding pinning tests (in the form of JUnit tests) to show that the functionality is unchanged by your modifications
2. Refactoring the method to be more performant while making sure your pinning tests still pass
3. Profiling (after) showing that your rewrite helped make your method more performant

The code is available under the src/ directory.

## How to Run SlowLifeGUI

1. Running GameOfLife. For Windows do (for running SlowLifeGUI with argument 5):
    ```
    run.bat 5
    ```
    For Mac / Linux do (for running SlowLifeGUI with argument 5):
    ```
    bash run.sh 5
    ```
1. Running the TestRunner on GameOfLifePinningTest. For Windows do:
    ```
    runTest.bat
    ```
    For Mac / Linux do:
    ```
    bash runTest.sh
    ```    

Alternatively, I've created an Eclipse project for you so you can use Eclipse to import the existing project.

## What do do

The program is an implementation of Conway's Game of Life
(https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life).  You can change the
state of a cell (from living to dead) by clicking on one of the buttons.  Cells
which are currently alive have an X and a red background; cells that are dead
now, but were at any point alive during the current run, will have a green
background.

There are several other buttons which invoke different features:

1. Run - this will run one iteration of the Game of Life
2. Run Continuous - This will run iterations until you press the Stop button.
3. Stop - This will stop the current "Run Continuous" run.  It will have no effect if the program is not running continuously already.
4. Write - This will write the state of the system to a backup file, to be loaded later.
5. Undo - This will undo the previous iteration.  It will only work for one iteration (that is, you cannot do multiple undos to get back multiple iterations).
6. Load - This will load a previously-saved backup file (created using the Write button) to the current world.
7. Clear - This will clear the current world.

The application accepts one command line argument, specifying the size of the
world (e.g., if you enter 10, then you will create a 10 x 10 world).

### Task 1: Profiling using VisualVM

For the purposes of performance testing, we will focus on a 5 X 5 world.  For
the initial pattern, we will use the "blinker" pattern shown in:  

https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life#Examples_of_patterns  

The actual pattern GIF is at:  

https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life#/media/File:Game_of_life_blinker.gif  

We will start from the vertical bar on a 5X5 matrix as shown in the GIF.

For an actual full performance test suite, we would have to try multiple world
sizes and multiple patterns but for the purposes of this deliverable, we will
focus on performance debugging only the above scenario.  As it happens, once we
debug the above scenario, the application will start running quickly for all
scenarios.

There are exactly **THREE** major performance issues with **THREE** methods in
the code.  They could be in any feature of the program!  I recommend you try
exploratory testing to try out different features to determine which features
may have performance problems before profiling the application.  There are
**TWO** features that have problems (that is, two buttons).  The three
performance problems are dispersed in those two features.

In order to determine the "hot spots" of the application, you will need to run
a profiler such as VisualVM (download at https://visualvm.github.io/).  Using a
profiler, determine the THREE methods you can modify to measurably increase the
speed of the application without modifying behavior.  Refer to Exercise 4 for a
detailed explanation of how to use VisualVM to profile an application.

Now there is one more step that you have to do on VisualVM not explained in
Exercise 4: you need to replace "GameOfLife" with "*" in the "Profile classes:"
window on the right before pressing on the "CPU" button to start profiling.
This instructs VisualVM to not only the GameOfLife class (the class with the
main method), but all classes in the application.  You did not need to do this
for the Exercise 4 MonkeySim application because it was single-threaded
application.  All code in a single-threaded application execute starting from
the main method, so the default behavior of VisualVM to instrument starting
from the main method class was just fine.  GameOfLife is a GUI application and
in a Java GUI application, there are multiple event handler threads running in
the background to handle events like button presses concurrently with the
application.  In this case, the code for these threads do not start from the
main method.

### Task 2: Writing Pinning Tests for the Three Methods

Before doing refactoring any method, you should create "pinning tests" (as
described in the section on legacy code earlier - please review the slides on
Writing Testable Code if you need a refresher).  These pinning tests should
check that the behavior of a modified method was not changed by your refactor.
The methods should work EXACTLY the same as before, except they should be
faster and take up less CPU time.  **There should be at least one pinning test
per method refactored.**

In general, a pinning test doesn't necessarily have to be a unit test; it can
be an end-to-end test that you slap on quickly for the purposes of refactoring
(without spending the effort to localize tests by mocking external objects).
The end-to-end pinning test is then gradually refined into more high quality
unit tests. Sometimes this 2-step process is necessary because sometimes you
cannot write high quality unit tests before refactoring to make the code more
testable (e.g. via dependency injection).  So you need a temporary end-to-end
pinning test to protect the code base meanwhile.  For this deliverable, there
is no reason you cannot write unit tests from the get-go for pinning tests as
the dependency injection(s) has already been done for you.  An example is the
**setCells** method in MainPanel.

Here are some requirements for your pinning tests:

1. You will use the 5 X 5 blinker pattern that I described above when a pattern
   is required:

   https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life#/media/File:Game_of_life_blinker.gif  

   The vertical bar pattern should be your precondition and the next horizontal
bar pattern should be your postcondition.  **For the postcondition, make sure you
check all 25 cells in the 5 X 5 pattern**.

1. You are required to localize each pinning unit test within the tested class
   as we did for Deliverable 2 (meaning it should not exercise any code from
external classes). You will have to use Mockito mock objects to achieve this.

1. Also, you may have to use behavior verification instead of state
   verification to test some methods because the state change happens within a
mocked external object.  Remember that you can use behavior verification only
on mocked objects (technically, you can use Mockito.verify on real objects too
using something called a Spy, but you won't need it for this deliverable).  You
will get point deductions if you don't use mock objects and behavior
verification appropriately.

1. Note that even though the class is named GameOfLifePinningTest, the methods
   you test will not necessarily come from the GameOfLife class.  You will
create whatever objects from whatever classes are necessary to test the three
refactored methods.  Hint: there is no reason for you to create a GameOfLife
object as there are no methods that you need to refactor there.

You will write all your pinning tests in the class GameOfLifePinningTest by
completing the TODOs.  Please heed the comments.  Just like for Deliverable 2,
you can add a Java stack trace to the error message to get information about
why your tests are failing by inserting the following line below
TestRunner.java line 24:

```
System.out.println(f.getTrace());
```

### Task 3: Refactor the Three Methods

Now refactor the three methods so that they are no longer performance problems.
If you look carefully, the three methods do a lot of wasted work for no reason.
It should be easy to refactor my removing that work.  Make sure that your
pinning tests pass after refactoring.

## Report Format

Please use the ReportTemplate.docx file provided in this directory to write
your report.  If you don't have a .docx compatible word processor, that's
perfectly fine as long as you follow the same organization.  A PDF version of
the file is at ReportTemplate.pdf.  Please keep the page separation.

The report should have a title page with:
* Your name(s)

ON A SEPARATE PAGE, write a brief report on the first feature you optimized.
Write the name of the feature, the methods you refactored, and a VisualVM
screenshot of method "Hot spots" *after* the refactoring.  Please only include
the "Hot spots" window in the interest of space.  Please refer to Exercise 4 on
how the Hot spots window looks like.

ON A SEPARATE PAGE, write a brief report on the second feature you optimized.
Write the name of the feature, the methods you refactored, and a VisualVM
screenshot of method "Hot spots" *after* the refactoring.  Please take care not
to click on any other feature while profiling (including the first feature).

There are two features with performance problems as I mentioned (as in two
buttons).  Make sure you include both of them.  One feature has two problematic
methods, another feature has one problematic method.

## Grading
* Report - 10%
* My performance tests pass (autograder) - 45%
* My pinning tests pass (autograder) - 15%
* Your pinning tests pass (autograder) - 15%
* Your pinning tests are written correctly - 15%

Please read grading_rubric.txt before submitting!

Note that 75% of your grade (besides the report) will be graded by GradeScope
autograding.  However, adjustments to your autograded score may follow if you
make a bad faith attempt at tricking the autograder (e.g. write a pinning test
that does not properly test the method you are refactoring).

## Submission

You will do one submissions per group for this deliverable as usual.

1. You will create a github repository just for deliverable 4.  Make sure you
   keep the repository *PRIVATE* so that nobody else can access your
repository.  Once you are done modifying code, don't forget to commit and push
your changes to the github repository.  When you are done, submit your github
repository to GradeScope at the "Deliverable 4 GitHub" link.  Once you submit,
GradeScope will run the autograder to grade you and give feedback.  If you get
deductions, fix your code based on the feedback and resubmit.  Repeat until you
don't get deductions.

1. Submit your report to GradeScope at the "Deliverable 4 Report" link.

## GradeScope Feedback

It is encouraged that you submit to GradeScope early and often.  Please use the
feedback you get on each submission to improve your code!

The GradeScope autograder works in 3 phases:

1. GameOfLife method performance tests (45.0):

    I run performance tests on each of the three methods you should
optimize.  If any of those methods time out after 10 ms, you get a -15
deduction.

1. GameOfLife method pinning tests (15.0):

    I run my own pinning tests on each of the three methods you should
optimize.  These pinning tests pass without you having to do anything
(obviously because they are meant to test existing behavior of legacy code).
And they should stay that way.  If any of the pinning tests fail, you get a
-5 deduction.

1. GameOfLifePinningTest method tests (15.0):

    I run the pinning tests you wrote (GameOfLifePinningTest) on your
implementation.  If any of the pinning tests fail, you get a -5 deduction.

1. GameOfLifePinningTest Mocking and Behavior Verification (0.0):

    This section gives you feedback on whether you did proper mocking and
behavior verification.  It does three test runs using your
GameOfLifePinningTest: 1) A test run on a defect-free implementation, 2) A
test run on GameOfLifeBuggy.jar with a bug injected into a mocked object,
and 3) A test run on GameOfLifeBuggy.jar with a bug injected into a method
that can only be tested using behavior verification.

    If you did a good job, the bug injected into the mocked object should
not cause any additional test failures since mocked object methods are never
executed (stubs are executed instead).  On the other hand, the bug injected
into the method tested using behavior verification should result in an
additional failure due to the behavior change.  If all goes well, you should
see the followimg lines at the end of this section:

    ```
    PASSED (5/5): Bug injected into mocked object did not cause additional test failures.
    PASSED (5/5): Behavior verification correctly detected change in behavior.
    ```

    If you see FAILED instead, you need to fix your tests.
GameOfLifeBuggy.jar is included in the repository if you want to do further
testing.  Try running runBuggyMock.bat and runBuggyBehavior.bat, to execute
the JAR file with mocked object bug injection and behavior verification
method bug injection respectively (Mac/Linux users can run the *.sh
versions).  If you try the vertical bar blinker patter specified on
GameOfLifePinningTest.java, you will immediately see that it is defective.
You can also try running your GameOfLifePinningTest on the buggy
implementation using runTestBuggyMock.bat and runTestBuggyBehavior.bat.  You
will see the same JUnit output given on the GradeScope feedback.

    Just because you got PASSED for both, it does not mean that you are
guaranteed to get points for that rubric item.  You may have passed simply
because you did not yet write the relevant test!  So in the end, points will
be assigned through manual grading (hence the 0 points assigned in the
autograder).  But if you wrote the tests and you see FAILED, then you most
definitely have a problem.

## Resources

* VisualVM Download:
https://visualvm.github.io/download.html

* VisualVM Documentation:
https://visualvm.github.io/documentation.html

Method profiling is not the only thing that VisualVM knows how to do.  It can
also profile overall CPU usage, heap memory usage, thread creation/termination,
class loading/unloading, Java just-in-time compiler activity, etc.  It can also
profile heap memory in a detailed way to show which types of objects are
filling the memory and where their allocation sites were.  And needless to say,
VisualVM is not the only profiling tool out there.

In the unlikely case you can't find what you are looking for in existing
profilers, you can even write your own profiler using the Java Virtual Machine
Tool Interface (JVMTI).  JVMTI is what was used to build VisualVM.

* Creating a Debugging and Profiling Agent with JVMTI
https://www.oracle.com/technical-resources/articles/javase/jvmti.html

* JVMTI Reference
https://docs.oracle.com/javase/8/docs/platform/jvmti/jvmti.html
