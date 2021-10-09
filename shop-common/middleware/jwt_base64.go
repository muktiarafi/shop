package middleware

import (
	"encoding/base64"
	"encoding/json"
	"errors"
	"net/http"

	"github.com/muktiarafi/shop/shop-common/auth"
	"github.com/muktiarafi/shop/shop-common/exception"
	"github.com/muktiarafi/shop/shop-common/web"
)

func JwtBase64(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		jwtBase64 := r.Header.Get("x-jwt")
		if jwtBase64 == "" {
			exc := exception.NewSingleMessageException(
				exception.EUNAUTHORIZED,
				"jwt header is empty",
				errors.New("jwt header is empty"),
			)
			web.SendError(w, exc)
			return
		}

		data, err := base64.StdEncoding.DecodeString(jwtBase64)
		if err != nil {
			exc := exception.NewSingleMessageException(
				exception.EUNAUTHORIZED,
				"jwt header is empty",
				errors.New("jwt header is empty"),
			)
			web.SendError(w, exc)
			return
		}

		userPayload := new(auth.UserPayload)
		if err := json.Unmarshal(data, userPayload); err != nil {
			exc := exception.NewSingleMessageException(
				exception.EINVALID,
				"malformed jwt base64 value",
				errors.New("malformed jwt base64 value"),
			)
			web.SendError(w, exc)
			return
		}

		ctx := auth.UserPayloadToContext(userPayload, r)

		next.ServeHTTP(w, r.WithContext(ctx))
	})
}
