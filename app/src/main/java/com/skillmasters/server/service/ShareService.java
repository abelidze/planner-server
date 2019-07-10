package com.skillmasters.server.service;

import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.springframework.stereotype.Service;
import com.skillmasters.server.model.Permission;

@Service
public class ShareService
{
  private final Map<String, CachedPermission> cache = new ConcurrentHashMap<>();
  private final long INVALIDATE_TIME = 3600000;

  private class CachedPermission
  {
    public long timestamp;
    public List<Permission> permissions;

    public CachedPermission(Permission permission)
    {
      this.permissions = Arrays.asList(permission);
      this.timestamp = new Date().getTime();
    }

    public CachedPermission(List<Permission> permissions)
    {
      this.permissions = permissions;
      this.timestamp = new Date().getTime();
    }
  }

  public String cachePermission(Permission permission)
  {
    String token = generateToken();
    cache.put(token, new CachedPermission(permission));
    return token;
  }

  public String cachePermissionList(List<Permission> permissions)
  {
    String token = generateToken();
    cache.put(token, new CachedPermission(permissions));
    return token;
  }

  public void revokeToken(String token)
  {
    this.cache.remove(token);
  }

  public List<Permission> validateToken(String token)
  {
    CachedPermission obj = this.cache.get(token);
    if (obj == null || new Date().getTime() - obj.timestamp > this.INVALIDATE_TIME) {
      revokeToken(token);
      return null;
    }
    List<Permission> copy = new ArrayList<Permission>();
    for (Permission permission : obj.permissions) {
      copy.add(permission.toBuilder().build());
    }
    return copy;
  }

  private String generateToken()
  {
    char[] alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    return NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, alphabet, 8);
    // String uuid = UUID.randomUUID().toString();
    // return Hashing.sha256().hashString(uuid, StandardCharsets.UTF_8).toString();
  }
}