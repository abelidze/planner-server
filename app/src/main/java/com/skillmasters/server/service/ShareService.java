package com.skillmasters.server.service;

import java.util.Date;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.nio.charset.StandardCharsets;

import com.google.common.hash.Hashing;
import org.springframework.stereotype.Service;
import com.skillmasters.server.model.Permission;

@Service
public class ShareService
{
  private final Map<String, CachedPermission> cache = new HashMap<>();
  private final long INVALIDATE_TIME = 3600000;

  private class CachedPermission
  {
    public long timestamp;
    public Permission permission;

    public CachedPermission(Permission permission)
    {
      this.permission = permission;
      this.timestamp = new Date().getTime();
    }
  }

  public String cachePermission(Permission permission)
  {
    String uuid = UUID.randomUUID().toString();
    String token = Hashing.sha256().hashString(uuid, StandardCharsets.UTF_8).toString();
    cache.put(token, new CachedPermission(permission));
    return token;
  }

  public void revokeToken(String token)
  {
    this.cache.remove(token);
  }

  public Permission validateToken(String token)
  {
    CachedPermission obj = this.cache.get(token);
    if (obj == null || new Date().getTime() - obj.timestamp > this.INVALIDATE_TIME) {
      revokeToken(token);
      return null;
    }
    return obj.permission;
  }
}