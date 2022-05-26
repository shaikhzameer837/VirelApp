package com.sdsmdg.harjot.rotatingtext.models;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.Arrays;

/**
 * Created by Harjot on 01-May-17.
 */

public class Rotatable {
    private int color = Color.BLACK;
    private String[] text;
    private int updateDuration = 2000;
    private int animationDuration = 1000;
    private int currentWordNumber;

    private Integer[] color_array={Color.BLACK};
    private boolean useArray = false;

    private float size = 24.0f;
    private int strokeWidth = -1;

    private Path pathIn, pathOut;

    private Interpolator interpolator = new LinearInterpolator();

    private Typeface typeface;

    private boolean isCenter = false;

    private boolean isUpdated = false;

    private int FPS = 60;

    private int nCycles = 0, countCycles = 0;
    private String initialWord = "";

    private boolean applyHorizontal = false;

    public Rotatable(int updateDuration, String... text) {
        this.updateDuration = updateDuration;
        this.text = text;
        currentWordNumber = -1;
    }

    public Rotatable(int color, int updateDuration, String... text) {
        this.color = color;
        this.updateDuration = updateDuration;
        this.text = text;
        currentWordNumber = -1;
    }

    public Rotatable(Integer[] color_array, int updateDuration, String... text) {
        this.color_array = color_array;
        this.updateDuration = updateDuration;
        this.text = text;
        currentWordNumber = -1;
        useArray = true;
    }

    public int getColor() {
        return color;
    }

    public boolean useArray() {
        return useArray;
    }

    public int getColorFromArray(int pos) {
        return color_array[pos];
    }

    public int colorArraySize() {
        return color_array.length;
    }

    public void setColor(int color) {
        this.color = color;
        setUpdated(true);
    }

    public int countCycles(boolean bool) {
        if(bool) {
            countCycles = countCycles + 1;
        }
        return countCycles;
    }

    public void setCycles(int val) {
        this.nCycles = val;
        countCycles = 0;
    }

    public int getCycles() {
        return nCycles;
    }

    public void setInitialWord(String initialWord) {
        this.initialWord = initialWord;
    }

    public String getInitialWord() {
        return initialWord;
    }

    public void setApplyHorizontal(Boolean bool) {
        applyHorizontal = bool;
    }

    public boolean getApplyHorizontal() {
        return applyHorizontal;
    }

    public String[] getText() {
        return text;
    }

    public int getWordCount() {
        return text.length;
    }

    public void setText(String... text) {
        this.text = text;
    }

    public String getTextAt(int index) {
        if (index >= text.length) {
            throw new ArrayIndexOutOfBoundsException("index exceeded number of words!!");
        } else {
            return text[index];
        }
    }

    public void setTextAt(int index, String word) {
        if (index >= text.length) {
            throw new ArrayIndexOutOfBoundsException("index exceeded number of words!!");
        } else {
            text[index] = word;
        }
    }

    public void addTextAt(int index, String word) {
        if (index >= text.length) {
            throw new ArrayIndexOutOfBoundsException("index exceeded number of words!!");
        } else {
            String copyArray[] = text;
            if (currentWordNumber >= index) {
                currentWordNumber++;
            }
            text = new String[text.length + 1];
            int i = 0;
            for (; i < index; i++) {
                text[i] = copyArray[i];
            }
            text[index] = word;
            i++;
            for (; i <= copyArray.length; i++) {
                text[i] = copyArray[i - 1];
            }
        }
    }

    public String[] peekNewTextAt(int index, String word) {
        if (index >= text.length) {
            throw new ArrayIndexOutOfBoundsException("index exceeded number of words!!");
        } else {
            String[] newSet = Arrays.copyOf(text, text.length);
            newSet[index] = word;
            return newSet;
        }
    }

    public String[] peekAddTextAt(int index, String word) {
        if (index >= text.length) {
            throw new ArrayIndexOutOfBoundsException("index exceeded number of words!!");
        } else {
            String[] newSet = new String[text.length + 1];
            int i = 0;
            for (; i < index; i++) {
                newSet[i] = text[i];
            }
            newSet[index] = word;
            i++;
            for (; i <= text.length; i++) {
                newSet[i] = text[i - 1];
            }
            return newSet;
        }
    }

    public int getUpdateDuration() {
        return updateDuration;
    }

    public void setUpdateDuration(int updateDuration) {
        this.updateDuration = updateDuration;
        setUpdated(true);
    }

    public int getNextWordNumber() {
        //provides next word number circularly
        currentWordNumber = (currentWordNumber + 1) % text.length;
        return currentWordNumber;
    }

    public String peekNextWord() {
        //doesnot increases currentwordnumber
        return text[(currentWordNumber + 1) % text.length];
    }

    public String getNextWord() {
        return text[getNextWordNumber()];
    }

    public String getCurrentWord() {
        return text[currentWordNumber];
    }

    public int getCurrentWordNumber() { return currentWordNumber; }

    public String getPreviousWord() {
        if (currentWordNumber <= 0)
            return text[(text.length - 1)];
        else
            return text[currentWordNumber - 1];
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
        setUpdated(true);
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public int getAnimationDuration() {
        return animationDuration;
    }

    public void setAnimationDuration(int animationDuration) {
        this.animationDuration = animationDuration;
        setUpdated(true);
    }

    public Path getPathIn() {
        return pathIn;
    }

    public void setPathIn(Path pathIn) {
        this.pathIn = pathIn;
    }

    public Path getPathOut() {
        return pathOut;
    }

    public void setPathOut(Path pathOut) {
        this.pathOut = pathOut;
    }

    public Interpolator getInterpolator() {
        return interpolator;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
        setUpdated(true);
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
        setUpdated(true);
    }

    public String getLargestWord() {
        String largest = "";
        for (String s : text) {
            if (s.length() > largest.length()) {
                largest = s;
            }
        }
        return largest;
    }

    public String getLargestWordWithSpace() {
        String largest = "";
        for (String s : text) {
            if (s.length() > largest.length()) {
                largest = s;
            }
        }
        return largest + " ";
    }

    public String peekLargestReplaceWord(int index, String newWord) {
        String[] newSet = peekNewTextAt(index, newWord);
        String largest = "";
        for (String s : newSet) {
            if (s.length() > largest.length()) {
                largest = s;
            }
        }
        return largest + " ";
    }

    public String peekLargestAddWord(int index, String newWord) {
        String[] newSet = peekAddTextAt(index, newWord);
        String largest = "";
        for (String s : newSet) {
            if (s.length() > largest.length()) {
                largest = s;
            }
        }
        return largest + " ";
    }

    public boolean isCenter() {
        return isCenter;
    }

    public void setCenter(boolean center) {
        isCenter = center;
        setUpdated(true);
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }

    public int getFPS() {
        return FPS;
    }

    public void setFPS(int FPS) {
        this.FPS = FPS;
    }

}
