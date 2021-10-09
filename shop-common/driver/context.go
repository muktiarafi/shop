package driver

import (
	"context"
	"time"
)

func NewDBContext(ctx context.Context) (context.Context, context.CancelFunc) {
	return context.WithTimeout(ctx, 3*time.Second)
}
