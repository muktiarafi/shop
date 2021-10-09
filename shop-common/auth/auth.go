package auth

import (
	"context"
	"errors"
	"net/http"

	"github.com/google/uuid"
	"github.com/muktiarafi/shop/shop-common/exception"
)

type UserPayload struct {
	ID    uuid.UUID `json:"id"`
	Roles []string  `json:"roles"`
}

type key struct{}

func UserPayloadFromContext(r *http.Request) (*UserPayload, error) {
	userPayload, ok := r.Context().Value(key{}).(*UserPayload)
	if !ok {
		return nil, &exception.Ex{
			Err: errors.New("missing user payload"),
		}
	}

	return userPayload, nil
}

func UserPayloadToContext(userPayload *UserPayload, r *http.Request) context.Context {
	return context.WithValue(r.Context(), key{}, userPayload)
}
