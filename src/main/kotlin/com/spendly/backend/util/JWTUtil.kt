package com.spendly.backend.util

import com.spendly.backend.entity.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key
import java.util.Date
import java.util.HashMap
import java.util.function.Function
import io.jsonwebtoken.io.Decoders

@Component
class JwtUtil(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val jwtExpirationInMs: Long
) {
    fun extractUserName(token: String): String {
        return extractClaim(token) { obj: Claims -> obj.subject }
    }

    fun generateToken(userDetails: UserDetails): String {
        val user: User = userDetails as User
        val claims = HashMap<String, Any?>()
        claims["authorities"] = userDetails.authorities
        claims["isEmailVerified"] = user.isEmailVerified
        claims["typ"] = "access-token"
        return generateToken(claims, userDetails)
    }

    fun generateRefreshToken(userDetails: UserDetails): String {
        val user: User = userDetails as User
        val claims = HashMap<String, Any?>()
        claims["authorities"] = userDetails.authorities
        claims["isEmailVerified"] = user.isEmailVerified
        claims["typ"] = "refresh-token"
        return generateRefreshToken(claims, userDetails)
    }

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val userName = extractUserName(token)
        return (userName == userDetails.username) && !isTokenExpired(token)
    }

    private fun <T> extractClaim(token: String, claimsResolvers: Function<Claims, T>): T {
        val claims = extractAllClaims(token)
        return claimsResolvers.apply(claims)
    }

    private fun generateToken(extraClaims: Map<String, Any?>, userDetails: UserDetails): String {
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30))
            .signWith(signingKey, SignatureAlgorithm.HS256).compact()
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }

    private fun extractExpiration(token: String): Date {
        return extractClaim(token) { obj: Claims -> obj.expiration }
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token)
            .body
    }

    private val signingKey: Key
        get() {
            val keyBytes = Decoders.BASE64.decode(secret)
            return Keys.hmacShaKeyFor(keyBytes)
        }

    private fun generateRefreshToken(extraClaims: Map<String, Any?>, userDetails: UserDetails): String {
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + jwtExpirationInMs))
            .signWith(signingKey, SignatureAlgorithm.HS256).compact()
    }
}
