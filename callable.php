<?php
// An example class
class MyClass
{
    /**
     * A test function
     *
     * First parameter must be an object of type OtherClass
     */
    public function test(OtherClass $otherclass) {
        echo $otherclass->var;
    }


    /**
     * Another test function
     *
     * First parameter must be an array
     */
    public function test_array(array $input_array) {
        print_r($input_array);
    }
    
    /**
     * First parameter must be iterator
     */
    public function test_interface(Traversable $iterator) {
        echo get_class($iterator);
    }
    
    /**
     * First parameter must be callable
     */
    public function test_callable(callable $callback, $data) {
        call_user_func($callback, $data);
    }
}

// Another example class
class OtherClass {
    public $var = 'Hello World';
}

// An instance of each class
$myclass = new MyClass;
$otherclass = new OtherClass;

// Fatal Error: Argument 1 must be an object of class OtherClass
$myclass->test('hello'); // TODO: error
echo "\n";

// Fatal Error: Argument 1 must be an instance of OtherClass
$foo = new stdClass;
$myclass->test($foo); // TODO: error
echo "\n";

// Fatal Error: Argument 1 must not be null
// $myclass->test(null);

// Works: Prints Hello World
$myclass->test($otherclass);
echo "\n";

// Fatal Error: Argument 1 must be an array
$myclass->test_array('a string'); //TODO: error
echo "\n";

// Works: Prints the array
$myclass->test_array(array('a', 'b', 'c'));
echo "\n";

// Works: Prints ArrayObject
$myclass->test_interface(new ArrayObject(array()));
echo "\n";

// Works: Prints int(1)
$myclass->test_callable('var_dump', 1);
?>
