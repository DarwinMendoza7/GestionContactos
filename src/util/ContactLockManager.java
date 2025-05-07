package util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
/*Clase utilitaria para gestionar el bloqueo (lock) de contactos por nombre, permitiendo que sólo una instancia/hilo pueda editar o
 eliminar un contacto a la vez*/
public class ContactLockManager {
    private static final ConcurrentHashMap<String, ReentrantLock> memoryLocks = new ConcurrentHashMap<>();
    private static final Map<String, Thread> locks = new HashMap<>();
    
    //Intenta adquirir el lock para el contacto seleccionado
    public static synchronized boolean tryLock(String contactName) {
        if (locks.containsKey(contactName)) {
            return locks.get(contactName) == Thread.currentThread(); //Si ya está bloqueado, solo permite si el lock es del mismo hilo
        }
        //Adquirir el lock para este hilo
        locks.put(contactName, Thread.currentThread());
        return true;
    }
    //Libera el lock del contacto si el hilo actual es el propietario
    public static synchronized void unlock(String contactName) {
        if (locks.get(contactName) == Thread.currentThread()) {
            locks.remove(contactName);
        }
    }
    //Verifica si el hilo actual posee el lock del contacto
    public static synchronized boolean hasLock(String contactName) {
        return locks.get(contactName) == Thread.currentThread();
    }
    
}