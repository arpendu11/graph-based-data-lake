package org.practice;

import org.practice.HomeResourceTest;

import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
public class NativeHomeResourceIT extends HomeResourceTest {

    // Execute the same tests but in native mode.
}