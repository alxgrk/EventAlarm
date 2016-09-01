/*
 * Created on 28.10.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.info;

public interface InfoObject extends Comparable<InfoObject> {
    Class<? extends InfoObject> getRuntimeType();
}
